package vlad.fp.lib;

import static vlad.fp.lib.Utils.unmatched;
import static vlad.fp.lib.Utils.voidF;
import static vlad.fp.lib.Utils.voidOf;

import vlad.fp.lib.function.Function;
import vlad.fp.lib.function.Supplier;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public abstract class Future<T> {

  public static <T> Future<T> now(T value) {
    return Now(value);
  }

  public static <T> Future<T> delay(Supplier<T> a) {
    return Suspend(() -> Now(a.apply()));
  }

  public static <T> Future<T> fork(Supplier<Future<T>> a, ExecutorService pool) {
    return join(apply(a, pool));
  }

  public static <T> Future<T> suspend(Supplier<Future<T>> f) {
    return Suspend(f);
  }

  public static <T> Future<T> async(Function<Function<T, Void>, Void> listen) {
    return Async(cb -> listen.apply(a -> cb.apply(a).run()));
  }

  public static <T> Future<T> apply(Supplier<T> a, ExecutorService pool) {
    return Async(voidF(cb -> pool.submit(Utils.logErrors(() -> cb.apply(a.apply()).run()))));
  }

  private static <T> Future<T> join(Future<Future<T>> future) {
    return future.flatMap(x -> x.map(t -> t));
  }

  public static <T> Future<T> schedule(Supplier<T> supplier, Duration delay, ScheduledExecutorService pool) {
    return Async(Utils.voidF(cb -> pool.schedule(
        () -> cb.apply(supplier.apply()).run(), delay.toMillis(), TimeUnit.MILLISECONDS)));
  }

  private Future() {
    // private constructor
  }

  private <S, R> R foldT(
      Function<Now<T>, R> nowCase,
      Function<Suspend<T>, R> suspendCase,
      Function<Async<T>, R> asyncCase,
      Function<BindSuspend<S, T>, R> bindSuspendCase,
      Function<BindAsync<S, T>, R> bindAsyncCase
  ) {
    switch (getType()) {
      case NOW:
        return nowCase.apply(asNow());
      case SUSPEND:
        return suspendCase.apply(asSuspend());
      case ASYNC:
        return asyncCase.apply(asAsync());
      case BIND_SUSPEND:
        return bindSuspendCase.apply(asBindSuspend());
      case BIND_ASYNC:
        return bindAsyncCase.apply(asBindAsync());
      default:
        throw new AssertionError();
    }
  }

  public <R> Future<R> flatMap(Function<T, Future<R>> f) {
    return foldT(
        now -> Suspend(() -> f.apply(now.value)),
        suspend -> BindSuspend(suspend.thunk, f),
        async -> BindAsync(async.onFinish, f),
        bindSuspend ->
            Suspend(() -> BindSuspend(bindSuspend.thunk, s -> bindSuspend.f.apply(s).flatMap(f))),
        bindAsync ->
            Suspend(() -> BindAsync(bindAsync.onFinish, s -> bindAsync.f.apply(s).flatMap(f)))
    );
  }

  public <R> Future<R> map(Function<T, R> f) {
    return flatMap(t -> Now(f.apply(t)));
  }

  public void listen(Function<T, Trampoline<Void>> cb) {
    step().foldT(
        now -> voidOf(cb.apply(now.value)::run),
        suspend -> unmatched(),
        async -> async.onFinish.apply(cb),
        bindSuspend -> unmatched(),
        bindAsync -> bindAsync.onFinish.apply(
            s -> Trampoline.delay(() -> bindAsync.f.apply(s)).map(x -> voidOf(() -> x.listen(cb)))
        )
    );
  }

  public Future<T> step() {
    return Tailrec.run(this, x -> x.foldT(
        Tailrec::finish,
        suspend -> Tailrec.next(suspend.thunk.apply()),
        Tailrec::finish,
        bindSuspend -> Tailrec.next(bindSuspend.thunk.apply().flatMap(bindSuspend.f)),
        Tailrec::finish
    ));
  }

  public Future<T> start() {
    CountDownLatch latch = new CountDownLatch(1);
    AtomicReference<T> result = new AtomicReference<T>();
    runAsync(a -> {
      result.set(a);
      latch.countDown();
      return null;
    });
    return delay(() -> {
      await(latch);
      return result.get();
    });
  }

  private void await(CountDownLatch latch) {
    try {
      latch.await();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException(e);
    }
  }

  public T run() {
    Function<Future<T>, T> other = f -> {
      CountDownLatch latch = new CountDownLatch(1);
      AtomicReference<T> result = new AtomicReference<>();
      runAsync(t -> { result.set(t); latch.countDown(); return null; });
      await(latch);
      return result.get();
    };
    return foldT(
        now -> now.value,
        other::apply,
        other::apply,
        other::apply,
        other::apply
    );
  }

  public void runAsync(Function<T, Void> cb) {
    listen(a -> Trampoline.done(cb.apply(a)));
  }

  private enum Type {
    NOW,
    SUSPEND,
    ASYNC,
    BIND_SUSPEND,
    BIND_ASYNC,
  }

  protected abstract Type getType();

  private static final class Now<T> extends Future<T> {
    private final T value;

    private Now(T value) {
      this.value = value;
    }

    @Override
    protected Type getType() {
      return Type.NOW;
    }

    @Override
    protected Now<T> asNow() {
      return this;
    }
  }

  private static <T> Now<T> Now(T value) {
    return new Now<>(value);
  }

  protected Now<T> asNow() {
    throw new AssertionError();
  }

  private static final class Suspend<T> extends Future<T> {
    private final Supplier<Future<T>> thunk;

    private Suspend(Supplier<Future<T>> thunk) {
      this.thunk = thunk;
    }

    @Override
    protected Type getType() {
      return Type.SUSPEND;
    }

    @Override
    protected Suspend<T> asSuspend() {
      return this;
    }
  }

  private static <T> Suspend<T> Suspend(Supplier<Future<T>> thunk) {
    return new Suspend<>(thunk);
  }

  protected Suspend<T> asSuspend() {
    throw new AssertionError();
  }

  private static final class Async<T> extends Future<T> {
    private final Function<Function<T, Trampoline<Void>>, Void> onFinish;

    private Async(Function<Function<T, Trampoline<Void>>, Void> onFinish) {
      this.onFinish = onFinish;
    }

    @Override
    protected Type getType() {
      return Type.ASYNC;
    }

    @Override
    protected Async<T> asAsync() {
      return this;
    }
  }

  private static <T> Async<T> Async(Function<Function<T, Trampoline<Void>>, Void> onFinish) {
    return new Async<>(onFinish);
  }

  protected Async<T> asAsync() {
    throw new AssertionError();
  }

  private static final class BindSuspend<S, T> extends Future<T> {
    private final Supplier<Future<S>> thunk;
    private final Function<S, Future<T>> f;

    private BindSuspend(Supplier<Future<S>> thunk, Function<S, Future<T>> f) {
      this.thunk = thunk;
      this.f = f;
    }

    @Override
    protected Type getType() {
      return Type.BIND_SUSPEND;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected BindSuspend<S, T> asBindSuspend() {
      return this;
    }
  }

  private static <S, T> BindSuspend<S, T> BindSuspend(
      Supplier<Future<S>> thunk,
      Function<S, Future<T>> f
  ) {
    return new BindSuspend<>(thunk, f);
  }

  protected  <S> BindSuspend<S, T> asBindSuspend() {
    throw new AssertionError();
  }

  private static final class BindAsync<S, T> extends Future<T> {
    private final Function<Function<S, Trampoline<Void>>, Void> onFinish;
    private final Function<S, Future<T>> f;

    private BindAsync(
        Function<Function<S, Trampoline<Void>>, Void> onFinish,
        Function<S, Future<T>> f
    ) {
      this.onFinish = onFinish;
      this.f = f;
    }

    @Override
    protected Type getType() {
      return Type.BIND_ASYNC;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected BindAsync<S, T> asBindAsync() {
      return this;
    }
  }

  private static <S, T> BindAsync<S, T> BindAsync(
      Function<Function<S, Trampoline<Void>>, Void> onFinish,
      Function<S, Future<T>> f) {
    return new BindAsync<>(onFinish, f);
  }

  protected  <S> BindAsync<S, T> asBindAsync() {
    throw new AssertionError();
  }

}

package vlad.fp.future;

import vlad.fp.Trampoline;
import vlad.fp.Unit;
import vlad.fp.higher.Parametrized;
import vlad.fp.tailrec.TailRec;
import vlad.fp.utils.Matcher;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class Future<A> implements Parametrized<Future,A> {

  public static <A> Future<A> now(A value) {
    return new Now<>(value);
  }

  public static <A> Future<A> delay(Supplier<A> a) {
    return suspend(() -> now(a.get()));
  }

  public static <A> Future<A> fork(Supplier<Future<A>> a, ExecutorService pool) {
    return join(apply(a, pool));
  }

  public static <A> Future<A> suspend(Supplier<Future<A>> f) {
    return new Suspend<>(f);
  }

  public static <A> Future<A> async(Function<Function<A, Unit>, Unit> listener) {
    return new Async<>(cb -> listener.apply(a -> cb.apply(a).run()));
  }

  public static <A> Future<A> apply(Supplier<A> a, ExecutorService pool) {
    return new Async<>(Unit.accept(cb -> pool.submit(logErrors(() -> cb.apply(a.get()).run()))));
  }

  private static <A> Future<A> join(Future<Future<A>> future) {
    return future.flatMap(x -> x.map(t -> t));
  }

  public static <A> Future<A> schedule(Supplier<A> supplier, Duration delay, ScheduledExecutorService pool) {
    return new Async<>(Unit.accept(cb -> pool.schedule(() -> cb.apply(supplier.get()).run(), delay.toMillis(), TimeUnit.MILLISECONDS)));
  }

  Future() {

  }

  public abstract <B, S> B match(
      Function<Now<A>, B> nowCase,
      Function<Suspend<A>, B> suspendCase,
      Function<Async<A>, B> asyncCase,
      Function<BindSuspend<S, A>, B> bindSuspendCase,
      Function<BindAsync<S, A>, B> bindAsyncCase);
  
  public <B> Future<B> flatMap(Function<A, Future<B>> f) {
    return match(
        now -> new Suspend<>(() -> f.apply(now.value())),
        suspend -> new BindSuspend<>(suspend.value(), f),
        async -> new BindAsync<>(async.listener(), f),
        bindSuspend -> new Suspend<>(() -> new BindSuspend<>(bindSuspend.prev(), s -> bindSuspend.function().apply(s).flatMap(f))),
        bindAsync -> new Suspend<>(() -> new BindAsync<>(bindAsync.listener(), s -> bindAsync.function().apply(s).flatMap(f)))
    );
  }

  public <B> Future<B> map(Function<A, B> f) {
    return flatMap(t -> now(f.apply(t)));
  }

  public void listen(Function<A, Trampoline<Unit>> cb) {
    step().match(
        now -> Unit.run(() -> cb.apply(now.value()).run()),
        suspend -> Matcher.unmatched(),
        async -> async.listener().apply(cb),
        bindSuspend -> Matcher.unmatched(),
        bindAsync -> bindAsync.listener().apply(s -> Trampoline.delay(() ->
            bindAsync.function().apply(s)).map(x -> Unit.run(() -> x.listen(cb)))
        )
    );
  }

  public Future<A> step() {
    return stepTailRec().eval();
  }

  private TailRec<Future<A>> stepTailRec() {
    return match(
        TailRec::done,
        suspend -> TailRec.suspend(() -> suspend.value().get().stepTailRec()),
        TailRec::done,
        bindSuspend -> TailRec.suspend(() -> bindSuspend.prev().get().flatMap(bindSuspend.function()).stepTailRec()),
        TailRec::done
    );
  }

  public Future<A> start() {
    CountDownLatch latch = new CountDownLatch(1);
    AtomicReference<A> result = new AtomicReference<>();

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

  public A run() {
    Function<Future<A>, A> other = f -> {
      CountDownLatch latch = new CountDownLatch(1);
      AtomicReference<A> result = new AtomicReference<>();
      runAsync(t -> { result.set(t); latch.countDown(); return null; });
      await(latch);
      return result.get();
    };
    return match(
        Now::value,
        other::apply,
        other::apply,
        other::apply,
        other::apply
    );
  }

  public void runAsync(Function<A, Unit> cb) {
    listen(a -> Trampoline.done(cb.apply(a)));
  }

  private static Runnable logErrors(Runnable runnable) {
    return () -> {
      try {
        runnable.run();
      } catch (Throwable t) {
        t.printStackTrace();
        throw t;
      }
    };
  }

}

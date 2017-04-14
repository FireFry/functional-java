package vlad.fp.lib;

import vlad.fp.lib.function.Function;
import vlad.fp.lib.function.Supplier;
import vlad.fp.lib.higher.Monad;
import vlad.fp.lib.higher.Parametrized;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

public final class Task<T> implements Parametrized<Task, T> {

  public static <T> Task<T> lift(Parametrized<Task, T> par) {
    return (Task<T>) par;
  }

  private final Future<Either<Throwable, T>> future;

  public static <T> Task<T> of(Future<Either<Throwable, T>> future) {
    return new Task<>(future);
  }

  private Task(Future<Either<Throwable, T>> future) {
    this.future = future;
  }

  public Future<Either<Throwable, T>> future() {
    return future;
  }

  public <R> Task<R> flatMap(Function<T, Task<R>> f) {
    Future<Either<Throwable, R>> rFuture = future.flatMap(e -> e.fold(
        t -> Future.now(Either.left(t)),
        r -> Try(() -> f.apply(r)).fold(
            t -> Future.now(Either.left(t)),
            task -> task.future
        )));
    return new Task<>(rFuture);
  }

  public <R> Task<R> map(Function<T, R> f) {
    return new Task<>(future.map(e -> e.flatMap(x -> Try(() -> f.apply(x)))));
  }

  public Task<Either<Throwable, T>> attempt() {
    return new Task<>(future.map(e -> e.fold(
        t -> Either.right(Either.left(t)),
        r -> Either.right(Either.right(r))))
    );
  }

  public Task<T> onFinish(Function<Option<Throwable>, Task<Void>> f) {
    return new Task<>(future.flatMap(e -> e.fold(
        t -> { f.apply(Option.some(t)); return Future.now(Either.left(t)); },
        r -> { f.apply(Option.none()); return Future.now(Either.right(r)); }
    )));
  }

  public Task<T> handle(Function<Throwable, Option<T>> f) {
    return handleWidth(th -> f.apply(th).map(Task::now));
  }

  public Task<T> handleWidth(Function<Throwable, Option<Task<T>>> f) {
    return attempt().flatMap(e -> e.fold(
        th -> f.apply(th).orElse(() -> fail(th)),
        Task::now
    ));
  }

  public Task<T> or(Task<T> t2) {
    return new Task<>(future.flatMap(e -> e.fold(
        th -> t2.future,
        x -> Future.now(Either.right(x))
    )));
  }

  public T run() {
    return future.run().fold(
        Utils::propagate,
        x -> x
    );
  }

  public Either<Throwable, T> attemptRun() {
    try { return future.run(); } catch (Throwable th) { return Either.left(th); }
  }

  public void runAsync(Function<Either<Throwable, T>, Void> f) {
    future.runAsync(f);
  }

  private static <T> Either<Throwable, T> Try(Supplier<T> supplier) {
    try {
      return Either.right(supplier.apply());
    } catch (Throwable t) {
      return Either.left(t);
    }
  }

  public static <T> Task<T> fail(Throwable th) {
    return new Task<>(Future.now(Either.left(th)));
  }

  public static <T> Task<T> now(T x) {
    return new Task<>(Future.now(Either.right(x)));
  }

  public static <T> Task<T> delay(Supplier<T> supplier) {
    return suspend(() -> now(supplier.apply()));
  }

  public static <T> Task<T> suspend(Supplier<Task<T>> supplier) {
    return new Task<>(Future.suspend(() -> Try(supplier).fold(
        th -> Future.now(Either.left(th)),
        t -> t.future
    )));
  }

  public static <T> Task<T> apply(Supplier<T> supplier, ExecutorService pool) {
    return new Task<>(Future.apply(() -> Try(supplier), pool));
  }

  public static <T> Task<T> async(Function<Function<Either<Throwable, T>, Void>, Void> register) {
    return new Task<>(Future.async(register));
  }

  public static <T> Task<T> fork(Supplier<Task<T>> supplier, ExecutorService pool) {
    return join(apply(supplier, pool));
  }

  public static <T> Task<T> join(Task<Task<T>> task) {
    return task.flatMap(x -> x.map(t -> t));
  }

  public static <T> Task<T> schedule(Supplier<T> supplier, Duration delay, ScheduledExecutorService pool) {
    return new Task<>(Future.schedule(() -> Try(supplier), delay, pool));
  }

  public static <T> Task<T> scheduleF(Supplier<Task<T>> supplier, Duration delay, ScheduledExecutorService pool) {
    return join(schedule(supplier, delay, pool));
  }

  public static final Monad<Task> MONAD = new Monad<Task>() {
    @Override
    public <T> Parametrized<Task, T> pure(T x) {
      return Task.now(x);
    }

    @Override
    public <T, R> Parametrized<Task, R> flatMap(Parametrized<Task, T> fa, Function<T, Parametrized<Task, R>> f) {
      return lift(fa).flatMap(x -> Task.lift(f.apply(x)));
    }
  };

}

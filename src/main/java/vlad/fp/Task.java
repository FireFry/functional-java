package vlad.fp;

import vlad.fp.either.Either;
import vlad.fp.future.Future;
import vlad.fp.higher.Parametrized;
import vlad.fp.maybe.Maybe;
import vlad.fp.utils.Unchecked;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Function;
import java.util.function.Supplier;

public final class Task<A> implements Parametrized<Task, A> {

  public static <A> Task<A> lift(Parametrized<Task, A> par) {
    return (Task<A>) par;
  }

  private final Future<Either<Throwable, A>> future;

  public static <A> Task<A> of(Future<Either<Throwable, A>> future) {
    return new Task<>(future);
  }

  private Task(Future<Either<Throwable, A>> future) {
    this.future = future;
  }

  public Future<Either<Throwable, A>> future() {
    return future;
  }

  public <B> Task<B> flatMap(Function<A, Task<B>> f) {
    Future<Either<Throwable, B>> rFuture = future.flatMap(e -> e.matchVal(
        t -> Future.now(Either.left(t)),
        r -> attempt(() -> f.apply(r)).matchVal(
            t -> Future.now(Either.left(t)),
            task -> task.future
        )));
    return new Task<>(rFuture);
  }

  public <B> Task<B> map(Function<A, B> f) {
    return new Task<>(future.map(e -> e.flatMap(x -> attempt(() -> f.apply(x)))));
  }

  public Task<Either<Throwable, A>> attempt() {
    return new Task<>(future.map(e -> e.matchVal(
        t -> Either.right(Either.left(t)),
        r -> Either.right(Either.right(r))))
    );
  }

  public Task<A> onFinish(Function<Maybe<Throwable>, Task<Unit>> f) {
    return new Task<>(future.flatMap(e -> e.matchVal(
        t -> { f.apply(Maybe.some(t)); return Future.now(Either.left(t)); },
        r -> { f.apply(Maybe.none()); return Future.now(Either.right(r)); }
    )));
  }

  public Task<A> handle(Function<Throwable, Maybe<A>> f) {
    return handleWidth(th -> f.apply(th).map(Task::now));
  }

  public Task<A> handleWidth(Function<Throwable, Maybe<Task<A>>> f) {
    return attempt().flatMap(e -> e.matchVal(
        th -> f.apply(th).matchVal(() -> fail(th), x -> x),
        Task::now
    ));
  }

  public Task<A> or(Task<A> t2) {
    return new Task<>(future.flatMap(e -> e.matchVal(
        th -> t2.future,
        x -> Future.now(Either.right(x))
    )));
  }

  public A run() {
    return future.run().matchVal(
        Unchecked::propagate,
        x -> x
    );
  }

  public Either<Throwable, A> attemptRun() {
    try { return future.run(); } catch (Throwable th) { return Either.left(th); }
  }

  public Unit runAsync(Function<Either<Throwable, A>, Unit> f) {
    future.runAsync(f);
    return Unit.UNIT;
  }

  private static <A> Either<Throwable, A> attempt(Supplier<A> supplier) {
    try {
      return Either.right(supplier.get());
    } catch (Throwable t) {
      return Either.left(t);
    }
  }

  public static <A> Task<A> fail(Throwable th) {
    return new Task<>(Future.now(Either.left(th)));
  }

  public static <A> Task<A> now(A x) {
    return new Task<>(Future.now(Either.right(x)));
  }

  public static <A> Task<A> delay(Supplier<A> supplier) {
    return suspend(() -> now(supplier.get()));
  }

  public static <A> Task<A> suspend(Supplier<Task<A>> supplier) {
    return new Task<>(Future.suspend(() -> attempt(supplier).matchVal(
        th -> Future.now(Either.left(th)),
        t -> t.future
    )));
  }

  public static <A> Task<A> apply(Supplier<A> supplier, ExecutorService pool) {
    return new Task<>(Future.apply(() -> attempt(supplier), pool));
  }

  public static <A> Task<A> async(Function<Function<Either<Throwable, A>, Unit>, Unit> register) {
    return new Task<>(Future.async(register));
  }

  public static <A> Task<A> fork(Supplier<Task<A>> supplier, ExecutorService pool) {
    return join(apply(supplier, pool));
  }

  public static <A> Task<A> join(Task<Task<A>> task) {
    return task.flatMap(x -> x.map(t -> t));
  }

  public static <A> Task<A> schedule(Supplier<A> supplier, Duration delay, ScheduledExecutorService pool) {
    return new Task<>(Future.schedule(() -> attempt(supplier), delay, pool));
  }

  public static <A> Task<A> scheduleF(Supplier<Task<A>> supplier, Duration delay, ScheduledExecutorService pool) {
    return join(schedule(supplier, delay, pool));
  }

}

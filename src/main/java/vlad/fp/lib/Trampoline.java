package vlad.fp.lib;

import vlad.fp.lib.function.Function;
import vlad.fp.lib.function.Supplier;
import vlad.fp.lib.higher.Monad;
import vlad.fp.lib.higher.Parametrized;

public final class Trampoline<T> implements Parametrized<Trampoline,T> {
  public static <T> Trampoline<T> lift(Parametrized<Trampoline, T> par) {
    return (Trampoline<T>) par;
  }

  private final Free<Supplier, T> free;

  private static <T> Trampoline<T> wrap(Free<Supplier, T> free) {
    return new Trampoline<>(free);
  }

  private Trampoline(Free<Supplier, T> free) {
    this.free = free;
  }

  public static <T> Trampoline<T> done(T value) {
    return wrap(Free.done(value));
  }

  public static <T> Trampoline<T> suspend(Supplier<Trampoline<T>> thunk) {
    Supplier<Free<Supplier, T>> functor = () -> thunk.apply().free;
    return wrap(Free.suspend(functor));
  }

  public static <T> Trampoline<T> delay(Supplier<T> supplier) {
    return wrap(Free.liftF(supplier));
  }

  public <R> Trampoline<R> flatMap(Function<T, Trampoline<R>> f) {
    return wrap(free.map(f).flatMap(o -> o.free));
  }

  public <R> Trampoline<R> map(Function<T, R> f) {
    return wrap(free.flatMap(t -> Free.done(f.apply(t))));
  }

  public T run() {
    return free.run(a -> Supplier.lift(a).apply(), Supplier.FUNCTOR);
  }

  public static final Monad<Trampoline> MONAD = new Monad<Trampoline>() {
    @Override
    public <T> Parametrized<Trampoline, T> pure(T x) {
      return done(x);
    }

    @Override
    public <T, R> Parametrized<Trampoline, R> flatMap(Parametrized<Trampoline, T> fa, Function<T, Parametrized<Trampoline, R>> f) {
      return lift(fa).flatMap(x -> lift(f.apply(x)));
    }
  };
}

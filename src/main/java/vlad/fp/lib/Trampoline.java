package vlad.fp.lib;

import vlad.fp.lib.function.Function;
import vlad.fp.lib.function.Supplier;

public final class Trampoline<T> {
  private final Free<Supplier, T> free;

  private static <T> Trampoline<T> Trampoline(Free<Supplier, T> free) {
    return new Trampoline<>(free);
  }

  private Trampoline(Free<Supplier, T> free) {
    this.free = free;
  }

  public static <T> Trampoline<T> done(T value) {
    return Trampoline(Free.done(value));
  }

  public static <T> Trampoline<T> suspend(Supplier<Trampoline<T>> thunk) {
    Supplier<Free<Supplier, T>> functor = () -> thunk.apply().free;
    return Trampoline(Free.suspend(functor));
  }

  public static <T> Trampoline<T> delay(Supplier<T> supplier) {
    return Trampoline(Free.liftF(Supplier.FUNCTOR, supplier));
  }

  public <R> Trampoline<R> flatMap(Function<T, Trampoline<R>> f) {
    return Trampoline(free.map(f).flatMap(o -> o.free));
  }

  public <R> Trampoline<R> map(Function<T, R> f) {
    return Trampoline(free.flatMap(t -> Free.done(f.apply(t))));
  }

  public T run() {
    return free.run(a -> Supplier.lift(a).apply(), Supplier.FUNCTOR);
  }
}

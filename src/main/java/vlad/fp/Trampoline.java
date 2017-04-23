package vlad.fp;

import vlad.fp.free.Free;
import vlad.fp.higher.Monad;
import vlad.fp.higher.Parametrized;
import vlad.fp.lifters.SupplierLifter;
import vlad.fp.monads.SupplierMonad;
import vlad.fp.monads.TrampolineMonad;

import java.util.function.Function;
import java.util.function.Supplier;

public final class Trampoline<A> implements Parametrized<Trampoline, A> {

  private final Free<Supplier, A> free;

  private Trampoline(Free<Supplier, A> free) {
    this.free = free;
  }

  public static <A> Trampoline<A> lift(Parametrized<Trampoline, A> par) {
    return (Trampoline<A>) par;
  }

  private static <A> Trampoline<A> wrap(Free<Supplier, A> free) {
    return new Trampoline<>(free);
  }

  public static <A> Trampoline<A> done(A value) {
    return wrap(Free.done(value));
  }

  public static <A> Trampoline<A> suspend(Supplier<Trampoline<A>> next) {
    return wrap(Free.suspend(SupplierLifter.unlift(() -> next.get().free)));
  }

  public static <A> Trampoline<A> delay(Supplier<A> supplier) {
    return wrap(Free.liftF(SupplierLifter.unlift(supplier)));
  }

  public <B> Trampoline<B> flatMap(Function<A, Trampoline<B>> f) {
    return wrap(free.map(f).flatMap(o -> o.free));
  }

  public <B> Trampoline<B> map(Function<A, B> f) {
    return wrap(free.flatMap(a -> Free.done(f.apply(a))));
  }

  public A run() {
    return free.run(SupplierMonad.INSTANCE, a -> SupplierLifter.lift(a).get());
  }

  public static Monad<Trampoline> monad() {
    return TrampolineMonad.INSTANCE;
  }

}

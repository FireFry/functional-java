package vlad.fp.monads;

import static vlad.fp.lifters.SupplierLifter.lift;
import static vlad.fp.lifters.SupplierLifter.unlift;

import vlad.fp.higher.Monad;
import vlad.fp.higher.Parametrized;

import java.util.function.Function;
import java.util.function.Supplier;

public enum SupplierMonad implements Monad<Supplier> {
  INSTANCE;

  @Override
  public <A> Parametrized<Supplier, A> pure(A x) {
    return unlift(() -> x);
  }

  @Override
  public <A, B> Parametrized<Supplier, B> flatMap(Parametrized<Supplier, A> fa, Function<A, Parametrized<Supplier, B>> f) {
    return f.apply(lift(fa).get());
  }

  @Override
  public <A, B> Parametrized<Supplier, B> map(Parametrized<Supplier, A> fa, Function<A, B> f) {
    return unlift(() -> f.apply(lift(fa).get()));
  }

  @Override
  public <A, B> Parametrized<Supplier, B> apply(Parametrized<Supplier, A> fa, Parametrized<Supplier, Function<A, B>> f) {
    return unlift(() -> lift(f).get().apply(lift(fa).get()));
  }
}

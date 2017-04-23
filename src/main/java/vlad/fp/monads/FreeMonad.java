package vlad.fp.monads;

import vlad.fp.free.Free;
import vlad.fp.higher.Monad;
import vlad.fp.higher.Parametrized;

import java.util.function.Function;

public final class FreeMonad<F> implements Monad<Parametrized<Free, F>> {
  private static final FreeMonad INSTANCE = new FreeMonad();

  @SuppressWarnings("unchecked")
  public static <F> FreeMonad<F> get() {
    return INSTANCE;
  }

  @Override
  public <A> Parametrized<Parametrized<Free, F>, A> pure(A x) {
    return Free.done(x);
  }

  @Override
  public <A, B> Parametrized<Parametrized<Free, F>, B> flatMap(Parametrized<Parametrized<Free, F>, A> fa, Function<A, Parametrized<Parametrized<Free, F>, B>> f) {
    return Free.lift(fa).flatMap(x -> Free.lift(f.apply(x)));
  }
}

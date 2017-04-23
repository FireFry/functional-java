package vlad.fp.monads;

import static vlad.fp.Trampoline.lift;

import vlad.fp.Trampoline;
import vlad.fp.higher.Monad;
import vlad.fp.higher.Parametrized;

import java.util.function.Function;

public enum TrampolineMonad implements Monad<Trampoline> {
  INSTANCE;

  @Override
  public <A> Parametrized<Trampoline, A> pure(A x) {
    return Trampoline.done(x);
  }

  @Override
  public <A, B> Parametrized<Trampoline, B> flatMap(Parametrized<Trampoline, A> fa, Function<A, Parametrized<Trampoline, B>> f) {
    return lift(fa).flatMap(x -> lift(f.apply(x)));
  }
}

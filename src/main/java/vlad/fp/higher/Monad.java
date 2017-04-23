package vlad.fp.higher;

import java.util.function.Function;

public interface Monad<F> extends Applicative<F> {

  <A, B> Parametrized<F, B> flatMap(Parametrized<F, A> fa, Function<A, Parametrized<F, B>> f);

  @Override
  default <A, B> Parametrized<F, B> map(Parametrized<F, A> fa, Function<A, B> f){
    return flatMap(fa, a -> pure(f.apply(a)));
  }

  @Override
  default <A, B> Parametrized<F, B> apply(Parametrized<F, A> fa, Parametrized<F, Function<A, B>> f) {
    return flatMap(f, g -> map(fa, g));
  }

}

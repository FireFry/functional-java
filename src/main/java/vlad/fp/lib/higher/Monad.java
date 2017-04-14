package vlad.fp.lib.higher;

import vlad.fp.lib.function.Function;

public interface Monad<F> extends Applicative<F> {

  <T, R> Parametrized<F, R> flatMap(Parametrized<F, T> fa, Function<T, Parametrized<F, R>> f);

  @Override
  default <T, R> Parametrized<F, R> map(Parametrized<F, T> fa, Function<T, R> f){
    return flatMap(fa, a -> pure(f.apply(a)));
  }

  @Override
  default <T, R> Parametrized<F, R> apply(Parametrized<F, T> fa, Parametrized<F, Function<T, R>> f) {
    return flatMap(f, g -> map(fa, g));
  }

}

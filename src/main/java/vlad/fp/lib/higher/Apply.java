package vlad.fp.lib.higher;

import vlad.fp.lib.function.Function;

public interface Apply<F> extends Functor<F> {

  <T, R> Parametrized<F, R> apply(Parametrized<F, T> fa, Parametrized<F, Function<T, R>> f);

}

package vlad.fp.lib.higher;

import vlad.fp.lib.function.Function;

public interface Functor<F> {

  <T, R> Parametrized<F, R> map(Parametrized<F, T> fa, Function<T, R> f);

}

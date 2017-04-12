package vlad.fp.lib;

import vlad.fp.lib.function.Supplier;
import vlad.fp.lib.function.Function;
import vlad.fp.lib.higher.Functor;
import vlad.fp.lib.higher.Parametrized;

public interface Monad<F> extends Functor<F> {

  <T> Parametrized<F, T> point(final Supplier<T> a);

  <T, R> Parametrized<F, R> flatMap(Parametrized<F, T> fa, Function<T, Parametrized<F, R>> f);

  @Override
  default <T, R> Parametrized<F, R> map(Parametrized<F, T> fa, Function<T, R> f){
    return flatMap(fa, a -> point(() -> f.apply(a)));
  }

}

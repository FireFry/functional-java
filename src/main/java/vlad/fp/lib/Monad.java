package vlad.fp.lib;

import vlad.fp.lib.function.Supplier;
import vlad.fp.lib.function.Function;
import vlad.fp.lib.functor.Functor;
import vlad.fp.lib.generic.Generic;

public interface Monad<F> extends Functor<F> {

  <T> Generic<F, T> point(final Supplier<T> a);

  <T, R> Generic<F, R> flatMap(Generic<F, T> fa, Function<T, Generic<F, R>> f);

  @Override
  default <T, R> Generic<F, R> map(Generic<F, T> fa, Function<T, R> f){
    return flatMap(fa, a -> point(() -> f.apply(a)));
  }

}

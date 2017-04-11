package vlad.fp.lib.functor;

import vlad.fp.lib.function.Function;
import vlad.fp.lib.generic.Generic;

public interface Functor<F> {
  <T, R> Generic<F, R> map(Generic<F, T> fa, Function<T, R> f);
}

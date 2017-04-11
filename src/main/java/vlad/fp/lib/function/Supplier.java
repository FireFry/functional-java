package vlad.fp.lib.function;

import vlad.fp.lib.functor.Functor;
import vlad.fp.lib.generic.Generic;

public interface Supplier<R> extends Generic<Supplier, R> {
  R apply();

  default <S> Supplier<S> map(Function<R, S> f) {
    return () -> f.apply(this.apply());
  }

  static <R> Supplier<R> lift(Generic<Supplier, R> generic) {
    return (Supplier<R>) generic;
  }

  Functor<Supplier> FUNCTOR = new Functor<Supplier>() {
    @Override
    public <T, R> Supplier<R> map(Generic<Supplier, T> fa, Function<T, R> f) {
      return lift(fa).map(f);
    }
  };
}

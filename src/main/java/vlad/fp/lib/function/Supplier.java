package vlad.fp.lib.function;

import vlad.fp.lib.higher.Functor;
import vlad.fp.lib.higher.Parametrized;

public interface Supplier<R> extends Parametrized<Supplier, R> {

  R apply();

  default <S> Supplier<S> map(Function<R, S> f) {
    return () -> f.apply(this.apply());
  }

  static <R> Supplier<R> lift(Parametrized<Supplier, R> parametrized) {
    return (Supplier<R>) parametrized;
  }

  Functor<Supplier> FUNCTOR = new Functor<Supplier>() {
    @Override
    public <T, R> Supplier<R> map(Parametrized<Supplier, T> fa, Function<T, R> f) {
      return lift(fa).map(f);
    }
  };

}

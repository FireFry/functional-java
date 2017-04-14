package vlad.fp.lib.higher;

import vlad.fp.lib.function.Function;
import vlad.fp.lib.tuple.Tuple2;

public interface Functor<F> {

  <T, R> Parametrized<F, R> map(Parametrized<F, T> fa, Function<T, R> f);

  static <F, T, R> Function<Parametrized<F, T>, Parametrized<F, R>> liftF(Functor<F> F, Function<T, R> f) {
    return fa -> F.map(fa, f);
  }

  static <F, T, R> Parametrized<F, Tuple2<T, R>> fproduct(Functor<F> F, Parametrized<F, T> fa, Function<T, R> f) {
    return F.map(fa, t -> Tuple2.of(t, f.apply(t)));
  }

  static <F, G> Functor<Parametrized<F, G>> compose(Functor<F> F, Functor<G> G) {
    return new Functor<Parametrized<F, G>>() {
      @Override
      public <T, R> Parametrized<Parametrized<F, G>, R> map(Parametrized<Parametrized<F, G>, T> fa, Function<T, R> f) {
        return Parametrized.left(fa, fg -> F.map(fg, x -> G.map(x, f)));
      }
    };
  }

}

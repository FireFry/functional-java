package vlad.fp.lib.higher;

import vlad.fp.lib.function.Function;

public interface Parametrized<F, T> {

  @SuppressWarnings("unchecked")
  static <F, G, T, R> Parametrized<Parametrized<F, G>, R> left(
      Parametrized<Parametrized<F, G>, T> fa,
      Function<Parametrized<F, Parametrized<G, T>>, Parametrized<F, Parametrized<G, R>>> f
  ) {
    return (Parametrized<Parametrized<F, G>, R>) f.apply((Parametrized<F, Parametrized<G, T>>) fa);
  }

  @SuppressWarnings("unchecked")
  static <F, G, T, R> Parametrized<F, Parametrized<G, R>> right(
      Parametrized<F, Parametrized<G, T>> fa,
      Function<Parametrized<Parametrized<F, G>, T>, Parametrized<Parametrized<F, G>, R>> f
  ) {
    return (Parametrized<F, Parametrized<G, R>>) f.apply((Parametrized<Parametrized<F, G>, T>) fa);
  }

}

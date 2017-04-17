package vlad.fp.lib;

import vlad.fp.lib.function.Function;
import vlad.fp.lib.higher.Functor;
import vlad.fp.lib.higher.Parametrized;

public final class Fix<F> implements Parametrized<Fix, F> {
  private final Parametrized<F, Fix<F>> unfix;

  public static <F> Fix<F> fix(Parametrized<F, Fix<F>> unfix) {
    return new Fix<>(unfix);
  }

  private Fix(Parametrized<F, Fix<F>> unfix) {
    this.unfix = unfix;
  }

  public <T> T fold(Functor<F> F, Function<Parametrized<F, T>, T> f) {
    return f.apply(F.map(unfix, fix -> fix.fold(F, f)));
  }

  public static <T, F> Fix<F> unfold(Functor<F> F, T data, Function<T, Parametrized<F, T>> f) {
    return fix(F.map(f.apply(data), arg -> unfold(F, arg, f)));
  }

  public static <F, T, R> R hylo(
      Functor<F> F,
      T data,
      Function<T, Parametrized<F, T>> coAlg,
      Function<Parametrized<F, R>, R> alg
  ) {
    return alg.apply(F.map(coAlg.apply(data), x -> hylo(F, x, coAlg, alg)));
  }
}

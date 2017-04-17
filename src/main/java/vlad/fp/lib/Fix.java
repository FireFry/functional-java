package vlad.fp.lib;

import vlad.fp.lib.higher.Algebra;
import vlad.fp.lib.higher.CoAlgebra;
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

  public <T> T fold(Functor<F> F, Algebra<F, T> f) {
    return f.apply(F.map(unfix, fix -> fix.fold(F, f)));
  }

  public static <T, F> Fix<F> unfold(Functor<F> F, T data, CoAlgebra<T, F> f) {
    return fix(F.map(f.apply(data), arg -> unfold(F, arg, f)));
  }

  public static <F, T, R> R hylo(Functor<F> F, T data, CoAlgebra<T, F> coAlg, Algebra<F, R> alg) {
    return alg.apply(F.map(coAlg.apply(data), x -> hylo(F, x, coAlg, alg)));
  }
}

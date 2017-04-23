package vlad.fp;

import vlad.fp.higher.Functor;
import vlad.fp.higher.Parametrized;

import java.util.function.Function;

public final class Fix<F> implements Parametrized<Fix, F> {
  private final Parametrized<F, Fix<F>> unfix;

  public static <F> Fix<F> fix(Parametrized<F, Fix<F>> unfix) {
    return new Fix<>(unfix);
  }

  private Fix(Parametrized<F, Fix<F>> unfix) {
    this.unfix = unfix;
  }

  public <A> A cata(Functor<F> F, Function<Parametrized<F, A>, A> algebra) {
    return algebra.apply(F.map(unfix, fix -> fix.cata(F, algebra)));
  }

  public static <T, F> Fix<F> ana(Functor<F> F, T data, Function<T, Parametrized<F, T>> coalgebra) {
    return fix(F.map(coalgebra.apply(data), arg -> ana(F, arg, coalgebra)));
  }

  public static <F, A, B> B hylo(Functor<F> F, A data, Function<A, Parametrized<F, A>> coalgebra, Function<Parametrized<F, B>, B> algebra) {
    return algebra.apply(F.map(coalgebra.apply(data), x -> hylo(F, x, coalgebra, algebra)));
  }
}

package vlad.fp.lib.higher;

import vlad.fp.lib.function.Function;

public interface CoAlgebra<T, F> extends Function<T, Parametrized<F, T>> {

}

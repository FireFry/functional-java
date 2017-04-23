package vlad.fp.higher;

import java.util.function.Function;

public interface Apply<F> extends Functor<F> {

  <A, B> Parametrized<F, B> apply(Parametrized<F, A> fa, Parametrized<F, Function<A, B>> f);

}

package vlad.fp.higher;

import java.util.function.Function;

public interface Functor<F> {

  <A, B> Parametrized<F, B> map(Parametrized<F, A> fa, Function<A, B> f);

}

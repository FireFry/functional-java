package vlad.fp.higher;

public interface Applicative<F> extends Apply<F> {

  <A> Parametrized<F, A> pure(A x);

}

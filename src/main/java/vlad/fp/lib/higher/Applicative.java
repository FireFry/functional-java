package vlad.fp.lib.higher;

public interface Applicative<F> extends Apply<F> {

  <T> Parametrized<F, T> pure(T x);

}

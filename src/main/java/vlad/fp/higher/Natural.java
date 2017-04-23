package vlad.fp.higher;

public interface Natural<F, G> {

  <A> Parametrized<G, A> apply(Parametrized<F, A> fa);

}

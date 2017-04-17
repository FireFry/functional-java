package vlad.fp.lib.higher;

public interface Natural<F, G> {
  <T> Parametrized<G, T> apply(Parametrized<F, T> fa);

  static <F> Natural<F, F> identity(){
    return new Natural<F, F>() {
      @Override
      public <T> Parametrized<F, T> apply(Parametrized<F, T> f) {
        return f;
      }
    };
  }
}

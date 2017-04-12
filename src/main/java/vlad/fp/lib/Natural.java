package vlad.fp.lib;

import vlad.fp.lib.higher.Parametrized;

public interface Natural<F, G> {
  <T> Parametrized<G, T> apply(Parametrized<F, T> fa);

  static <F> Natural<F, F> id(){
    return new Natural<F, F>() {
      @Override
      public <T> Parametrized<F, T> apply(Parametrized<F, T> f) {
        return f;
      }
    };
  }
}

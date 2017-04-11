package vlad.fp.lib;

import vlad.fp.lib.generic.Generic;

public interface Natural<F, G> {
  <T> Generic<G, T> apply(Generic<F, T> fa);

  static <F> Natural<F, F> id(){
    return new Natural<F, F>() {
      @Override
      public <T> Generic<F, T> apply(Generic<F, T> f) {
        return f;
      }
    };
  }
}

package vlad.fp.free_example;

import vlad.fp.lib.generic.Generic;

public interface Console<F> {

  Generic<F, String> readLine();

  Generic<F, Void> writeLine(String s);

}

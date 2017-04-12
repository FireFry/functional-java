package vlad.fp.free_example;

import vlad.fp.lib.higher.Parametrized;

public interface Console<F> {

  Parametrized<F, String> readLine();

  Parametrized<F, Void> writeLine(String s);

}

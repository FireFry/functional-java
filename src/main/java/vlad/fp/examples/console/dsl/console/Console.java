package vlad.fp.examples.console.dsl.console;

import vlad.fp.lib.higher.Parametrized;

public interface Console<F> extends Parametrized<Console, F> {

  Parametrized<F, String> readLine();

  Parametrized<F, String> writeLine(String s);

}

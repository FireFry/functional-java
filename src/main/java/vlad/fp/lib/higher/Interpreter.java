package vlad.fp.lib.higher;

import vlad.fp.lib.Free;
import vlad.fp.lib.higher.Natural;
import vlad.fp.lib.higher.Parametrized;

public interface Interpreter<F, G> extends Natural<F, Parametrized<Free, G>> {

}

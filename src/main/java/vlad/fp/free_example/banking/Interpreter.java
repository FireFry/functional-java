package vlad.fp.free_example.banking;

import vlad.fp.lib.Free;
import vlad.fp.lib.Natural;
import vlad.fp.lib.higher.Parametrized;

interface Interpreter<F, G> extends Natural<F,Parametrized<Free,G>> {

}

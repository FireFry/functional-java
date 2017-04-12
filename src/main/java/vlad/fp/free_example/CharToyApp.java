package vlad.fp.free_example;

import static vlad.fp.free_example.CharToy.bell;
import static vlad.fp.free_example.CharToy.done;
import static vlad.fp.free_example.CharToy.output;

import vlad.fp.lib.Free;
import vlad.fp.lib.generic.Generic;
import vlad.fp.lib.Monad;

public final class CharToyApp {

  public static void main(final String[] args){
    final Free<CharToy, Void> program =
      output('A') .flatMap( v1 ->
      bell() .flatMap( v2 ->
      output('B') .flatMap( v3 ->
      done() )));

    System.out.println(showProgram(program));
  }

  static <R> String showProgram(Free<CharToy, R> program) { return
    program.fold(CharToy.functor,
        l -> CharToy.lift(l).fold(
            (a, next) -> "output " + a + "\n" + showProgram(next),
            (next -> "bell " + "\n" + showProgram(next)),
            "done\n"
        ),
        r -> "return " + r + "\n"
    );
  }

  static <F> Generic<F, Void> myMonadicProgram(Console<F> console, Monad<F> M) {
    return M.flatMap(
        console.readLine(), a ->
        console.writeLine("You entered: " + a));
  }

}

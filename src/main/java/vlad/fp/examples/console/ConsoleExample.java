package vlad.fp.examples.console;

import vlad.fp.examples.console.dsl.console.Console;
import vlad.fp.examples.console.dsl.console.ConsoleF;
import vlad.fp.examples.console.exec.ConsoleIO;
import vlad.fp.lib.Free;
import vlad.fp.lib.Monad;
import vlad.fp.lib.Trampoline;
import vlad.fp.lib.function.Function;
import vlad.fp.lib.higher.Parametrized;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class ConsoleExample {

  private static <F> Parametrized<F, String> program(Monad<F> m, Console<F> console) {
    return m.flatMap(
        console.writeLine("Enter first line"), u1 -> m.flatMap(
        console.readLine(), firstLine -> m.flatMap(
        console.writeLine("Enter second line"), u2 -> m.flatMap(
        console.readLine(), secondLine -> m.map(
        console.writeLine("You entered: \"" + firstLine + "\" and \"" + secondLine + "\""), Function.identity())))));
  }

  private static Free<ConsoleF, String> consoleFProgram() {
    return Free.lift(program(Free.freeMonad(), ConsoleF.consoleFree(ConsoleF.FUNCTOR, ConsoleF.CONSOLE)));
  }

  public static void main(String[] args) {
    PrintWriter printWriter = new PrintWriter(System.out);
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
    ConsoleIO consoleIO = new ConsoleIO(printWriter, bufferedReader);
    Trampoline<String> trampoline = Trampoline.lift(consoleFProgram().foldMap(ConsoleF.FUNCTOR, Trampoline.MONAD, consoleIO));
    trampoline.run();
  }

}

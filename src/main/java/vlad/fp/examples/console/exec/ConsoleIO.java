package vlad.fp.examples.console.exec;

import vlad.fp.examples.console.dsl.console.ConsoleF;
import vlad.fp.lib.higher.Natural;
import vlad.fp.lib.Trampoline;
import vlad.fp.lib.Utils;
import vlad.fp.lib.higher.Parametrized;

import java.io.BufferedReader;
import java.io.PrintWriter;

public final class ConsoleIO implements Natural<ConsoleF, Trampoline> {
  private final PrintWriter printWriter;
  private final BufferedReader bufferedReader;

  public ConsoleIO(PrintWriter printWriter, BufferedReader bufferedReader) {
    this.printWriter = printWriter;
    this.bufferedReader = bufferedReader;
  }

  @Override
  public <T> Parametrized<Trampoline, T> apply(Parametrized<ConsoleF, T> fa) {
    return ConsoleF.lift(fa).foldT(
        readLine -> Trampoline.delay(Utils.unchecked(bufferedReader::readLine)).map(readLine.next),
        writeLine -> Trampoline.delay(() -> {
          printWriter.println(writeLine.s);
          printWriter.flush();
          return writeLine.s;
        }).map(writeLine.next)
    );
  }
}

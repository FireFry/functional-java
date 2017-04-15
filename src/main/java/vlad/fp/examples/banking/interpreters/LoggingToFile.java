package vlad.fp.examples.banking.interpreters;

import vlad.fp.examples.banking.dsl.file.AppendToFile;
import vlad.fp.examples.banking.dsl.file.FileF;
import vlad.fp.examples.banking.dsl.logging.LoggingF;
import vlad.fp.lib.Free;
import vlad.fp.lib.Interpreter;
import vlad.fp.lib.higher.Parametrized;

public enum LoggingToFile implements Interpreter<LoggingF, FileF> {
  INTERPRETER;

  @Override
  public <T> Free<FileF, T> apply(Parametrized<LoggingF, T> fa) {
    return LoggingF.lift(fa).foldT(
        log -> Free.liftF(new AppendToFile<>("app.log", log.msg, log.next))
    );
  }
}

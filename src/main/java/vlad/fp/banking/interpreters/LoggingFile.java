package vlad.fp.banking.interpreters;

import vlad.fp.banking.dsl.file.AppendToFile;
import vlad.fp.banking.dsl.file.FileF;
import vlad.fp.banking.dsl.logging.LoggingF;
import vlad.fp.lib.Free;
import vlad.fp.lib.higher.Parametrized;

public enum LoggingFile implements Interpreter<LoggingF, FileF> {
  INSTANCE;

  @Override
  public <T> Free<FileF, T> apply(Parametrized<LoggingF, T> fa) {
    return LoggingF.lift(fa).foldT(
        log -> Free.liftF(FileF.FUNCTOR, new AppendToFile<>("app.log", log.msg))
    );
  }
}

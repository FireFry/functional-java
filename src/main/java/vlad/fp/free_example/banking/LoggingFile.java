package vlad.fp.free_example.banking;

import vlad.fp.lib.Free;
import vlad.fp.lib.higher.Parametrized;

class LoggingFile implements Interpreter<LoggingF, FileF> {
  @Override
  public <T> Free<FileF, T> apply(Parametrized<LoggingF, T> fa) {
    return LoggingF.lift(fa).foldT(
        log -> Free.liftF(FileF.FUNCTOR, new FileF.AppendToFile<>("app.log", log.msg))
    );
  }
}

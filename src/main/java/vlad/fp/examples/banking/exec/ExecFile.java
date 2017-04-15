package vlad.fp.examples.banking.exec;

import vlad.fp.examples.banking.dsl.file.FileF;
import vlad.fp.lib.Natural;
import vlad.fp.lib.Task;
import vlad.fp.lib.Unit;
import vlad.fp.lib.higher.Parametrized;

public enum ExecFile implements Natural<FileF, Task> {
  INSTANCE;

  @Override
  public <T> Task<T> apply(Parametrized<FileF, T> fa) {
    return FileF.lift(fa).foldT(
        append -> Task.delay(() -> {
          System.out.println("Writing to " + append.fileName + ": " + append.string);
          return Unit.UNIT;
        }).map(append.next)
    );
  }
}

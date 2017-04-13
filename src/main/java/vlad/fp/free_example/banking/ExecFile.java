package vlad.fp.free_example.banking;

import vlad.fp.free_example.banking.file.FileF;
import vlad.fp.lib.Free;
import vlad.fp.lib.Natural;
import vlad.fp.lib.Task;
import vlad.fp.lib.Unit;
import vlad.fp.lib.higher.Parametrized;

class ExecFile implements Natural<FileF, Task> {
  @Override
  public <T> Task<T> apply(Parametrized<FileF, T> fa) {
    return FileF.lift(fa).foldT(
        append -> Task.delay(() -> {
          System.out.println("Writing to " + append.fileName + ": " + append.string);
          return (T) Free.done(Unit.UNIT); // TODO: figure this out
        })
    );
  }
}

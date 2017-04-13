package vlad.fp.banking.exec;

import vlad.fp.banking.dsl.socket.SocketF;
import vlad.fp.lib.Natural;
import vlad.fp.lib.Task;
import vlad.fp.lib.higher.Parametrized;

public enum ExecSocket implements Natural<SocketF, Task> {
  INSTANCE;

  @Override
  public <T> Parametrized<Task, T> apply(Parametrized<SocketF, T> fa) {
    return SocketF.lift(fa).fold(x -> Task.delay(() -> x));
  }
}

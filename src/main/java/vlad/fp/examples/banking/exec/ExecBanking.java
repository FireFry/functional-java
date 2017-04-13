package vlad.fp.examples.banking.exec;

import vlad.fp.examples.banking.interpreters.BankingToLogging;
import vlad.fp.examples.banking.interpreters.BankingToProtocol;
import vlad.fp.examples.banking.interpreters.LoggingToFile;
import vlad.fp.examples.banking.interpreters.ProtocolToSocket;
import vlad.fp.examples.banking.dsl.banking.BankingF;
import vlad.fp.examples.banking.dsl.file.FileF;
import vlad.fp.examples.banking.dsl.halt.Halt;
import vlad.fp.examples.banking.dsl.logging.LoggingF;
import vlad.fp.examples.banking.dsl.protocol.ProtocolF;
import vlad.fp.examples.banking.dsl.socket.SocketF;
import vlad.fp.lib.Free;
import vlad.fp.lib.Natural;
import vlad.fp.lib.Task;
import vlad.fp.lib.Unit;
import vlad.fp.lib.higher.Parametrized;

public enum ExecBanking implements Natural<BankingF, Task> {
  INSTANCE;

  @Override
  public <T> Parametrized<Task, T> apply(Parametrized<BankingF, T> fa) {
    Free<Parametrized<Halt, LoggingF>, T> logging = BankingToLogging.INTERPRETER.apply(BankingF.lift(fa));
    Free<LoggingF, Unit> loggingUnhalt = Halt.unhalt(LoggingF.FUNCTOR, logging);
    Free<FileF, Unit> file = Free.lift(loggingUnhalt.foldMap(LoggingF.FUNCTOR, Free.freeMonad(), LoggingToFile.INTERPRETER));
    return Task.lift(file.foldMap(FileF.FUNCTOR, Task.MONAD, ExecFile.INSTANCE)).flatMap(v -> {
      Free<ProtocolF, T> protocol = BankingToProtocol.INTERPRETER.apply(BankingF.lift(fa));
      Free<SocketF, T> socket = Free.lift(protocol.foldMap(ProtocolF.FUNCTOR, Free.freeMonad(), ProtocolToSocket.INTERPRETER));
      return Task.lift(socket.foldMap(SocketF.FUNCTOR, Task.MONAD, ExecSocket.INSTANCE));
    });
  }
}

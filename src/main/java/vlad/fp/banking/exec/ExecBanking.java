package vlad.fp.banking.exec;

import vlad.fp.banking.interpreters.BankingLogging;
import vlad.fp.banking.interpreters.BankingProtocol;
import vlad.fp.banking.interpreters.LoggingFile;
import vlad.fp.banking.interpreters.ProtocolSocket;
import vlad.fp.banking.dsl.banking.BankingF;
import vlad.fp.banking.dsl.file.FileF;
import vlad.fp.banking.dsl.halt.Halt;
import vlad.fp.banking.dsl.logging.LoggingF;
import vlad.fp.banking.dsl.protocol.ProtocolF;
import vlad.fp.banking.dsl.socket.SocketF;
import vlad.fp.lib.Free;
import vlad.fp.lib.Natural;
import vlad.fp.lib.Task;
import vlad.fp.lib.Unit;
import vlad.fp.lib.higher.Parametrized;

public enum ExecBanking implements Natural<BankingF, Task> {
  INSTANCE;

  @Override
  public <T> Parametrized<Task, T> apply(Parametrized<BankingF, T> fa) {
    BankingF<T> banking = BankingF.lift(fa);
    Free<Parametrized<Halt, LoggingF>, T> logging = Free.lift(BankingLogging.INSTANCE.apply(banking));
    Free<LoggingF, Unit> loggingUnhalt = Halt.unhalt(LoggingF.FUNCTOR, logging);
    Free<FileF, Unit> file = Free.lift(loggingUnhalt.foldMap(LoggingF.FUNCTOR, Free.freeMonad(), LoggingFile.INSTANCE));
    return Task.lift(file.foldMap(FileF.FUNCTOR, Task.MONAD, ExecFile.INSTANCE)).flatMap(v -> {
      Free<ProtocolF, T> protocol = Free.lift(BankingProtocol.INSTANCE.apply(banking));
      Free<SocketF, T> socket = Free.lift(protocol.foldMap(ProtocolF.FUNCTOR, Free.freeMonad(), ProtocolSocket.INSTANCE));
      return Task.lift(socket.foldMap(SocketF.FUNCTOR, Task.MONAD, ExecSocket.INSTANCE));
    });
  }
}

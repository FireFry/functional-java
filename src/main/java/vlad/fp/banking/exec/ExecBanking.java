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

public class ExecBanking implements Natural<BankingF, Task> {
  private static final ExecFile execFile = new ExecFile();
  private static final ExecSocket execSocket = new ExecSocket();
  private static final LoggingFile loggingFile = new LoggingFile();
  private static final BankingLogging bankingLogging = new BankingLogging();
  private static final BankingProtocol bankingProtocol = new BankingProtocol();
  private static final ProtocolSocket protocolSocket = new ProtocolSocket();

  @Override
  public <T> Parametrized<Task, T> apply(Parametrized<BankingF, T> fa) {
    BankingF<T> banking = BankingF.lift(fa);
    Free<Parametrized<Halt, LoggingF>, T> logging = Free.lift(bankingLogging.apply(banking));
    Free<LoggingF, Unit> loggingUnhalt = Halt.unhalt(LoggingF.FUNCTOR, logging);
    Free<FileF, Unit> file = Free.lift(loggingUnhalt.foldMap(LoggingF.FUNCTOR, Free.freeMonad(), loggingFile));
    return Task.lift(file.foldMap(FileF.FUNCTOR, Task.MONAD, execFile)).flatMap(v -> {
      Free<ProtocolF, T> protocol = Free.lift(bankingProtocol.apply(banking));
      Free<SocketF, T> socket = Free.lift(protocol.foldMap(ProtocolF.FUNCTOR, Free.freeMonad(), protocolSocket));
      return Task.lift(socket.foldMap(SocketF.FUNCTOR, Task.MONAD, execSocket));
    });
  }
}

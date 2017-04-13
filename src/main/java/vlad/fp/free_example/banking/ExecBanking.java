package vlad.fp.free_example.banking;

import vlad.fp.free_example.banking.dsl.banking.BankingF;
import vlad.fp.free_example.banking.dsl.file.FileF;
import vlad.fp.free_example.banking.dsl.halt.Halt;
import vlad.fp.free_example.banking.dsl.logging.LoggingF;
import vlad.fp.free_example.banking.dsl.protocol.ProtocolF;
import vlad.fp.free_example.banking.dsl.socket.SocketF;
import vlad.fp.lib.Free;
import vlad.fp.lib.Natural;
import vlad.fp.lib.Task;
import vlad.fp.lib.Unit;
import vlad.fp.lib.higher.Parametrized;

class ExecBanking implements Natural<BankingF, Task> {
  private static final Interpreter<BankingF, Parametrized<Halt, LoggingF>> bankingLogging = new BankingLogging();
  private static final Interpreter<LoggingF, FileF> loggingFile = new LoggingFile();
  private static final Natural<FileF, Task> execFile = new ExecFile();
  private static final Interpreter<BankingF, ProtocolF> bankingProtocol = new BankingProtocol();
  private static final Interpreter<ProtocolF, SocketF> protocolSocket = new ProtocolSocket();
  private static final Natural<SocketF, Task> execSocket = new ExecSocket();

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

package vlad.fp.banking.interpreters;

import vlad.fp.banking.dsl.protocol.ProtocolF;
import vlad.fp.banking.dsl.socket.JustReturn;
import vlad.fp.banking.dsl.socket.SocketF;
import vlad.fp.lib.Free;
import vlad.fp.lib.higher.Parametrized;

public enum ProtocolToSocket implements Interpreter<ProtocolF, SocketF> {
  INSTANCE;

  @Override
  public <T> Parametrized<Parametrized<Free, SocketF>, T> apply(Parametrized<ProtocolF, T> fa) {
    return ProtocolF.lift(fa).foldT(a -> Free.liftF(SocketF.FUNCTOR, new JustReturn<>(a.value, a.next)));
  }
}

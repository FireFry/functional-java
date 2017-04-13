package vlad.fp.examples.banking.interpreters;

import vlad.fp.examples.banking.dsl.protocol.ProtocolF;
import vlad.fp.examples.banking.dsl.socket.JustReturn;
import vlad.fp.examples.banking.dsl.socket.SocketF;
import vlad.fp.lib.Free;
import vlad.fp.lib.Interpreter;
import vlad.fp.lib.higher.Parametrized;

public enum ProtocolToSocket implements Interpreter<ProtocolF, SocketF> {
  INTERPRETER;

  @Override
  public <T> Parametrized<Parametrized<Free, SocketF>, T> apply(Parametrized<ProtocolF, T> fa) {
    return ProtocolF.lift(fa).foldT(a -> Free.liftF(SocketF.FUNCTOR, new JustReturn<>(a.value, a.next)));
  }
}
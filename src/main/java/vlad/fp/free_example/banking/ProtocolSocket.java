package vlad.fp.free_example.banking;

import vlad.fp.free_example.banking.dsl.protocol.ProtocolF;
import vlad.fp.free_example.banking.dsl.socket.JustReturn;
import vlad.fp.free_example.banking.dsl.socket.SocketF;
import vlad.fp.lib.Free;
import vlad.fp.lib.higher.Parametrized;

public final class ProtocolSocket implements Interpreter<ProtocolF, SocketF> {
  @Override
  public <T> Parametrized<Parametrized<Free, SocketF>, T> apply(Parametrized<ProtocolF, T> fa) {
    return ProtocolF.lift(fa).fold(a -> Free.liftF(SocketF.FUNCTOR, new JustReturn<>(a)));
  }
}

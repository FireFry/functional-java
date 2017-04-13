package vlad.fp.free_example.banking.protocol;

import vlad.fp.lib.function.Function;
import vlad.fp.lib.higher.Functor;
import vlad.fp.lib.higher.Parametrized;

public abstract class ProtocolF<T> implements Parametrized<ProtocolF, T> {
  public static <T> ProtocolF<T> lift(Parametrized<ProtocolF, T> par) {
    return (ProtocolF<T>) par;
  }

  ProtocolF() {}

  public abstract <R> ProtocolF<R> map(Function<T, R> f);

  public abstract <R> R fold(Function<T, R> justReturnCase);

  public static final Functor<ProtocolF> FUNCTOR = new Functor<ProtocolF>() {
    @Override
    public <T, R> Parametrized<ProtocolF, R> map(Parametrized<ProtocolF, T> fa, Function<T, R> f) {
      return lift(fa).map(f);
    }
  };
}

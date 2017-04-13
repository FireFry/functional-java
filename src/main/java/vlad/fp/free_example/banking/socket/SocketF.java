package vlad.fp.free_example.banking.socket;

import vlad.fp.lib.function.Function;
import vlad.fp.lib.higher.Functor;
import vlad.fp.lib.higher.Parametrized;

public abstract class SocketF<T> implements Parametrized<SocketF, T> {
  public static <T> SocketF<T> lift(Parametrized<SocketF, T> par) {
    return (SocketF<T>) par;
  }

  SocketF() {}

  public abstract <R> SocketF<R> map(Function<T, R> f);

  public abstract <R> R fold(Function<T, R> justReturnCase);

  public static final Functor<SocketF> FUNCTOR = new Functor<SocketF>() {
    @Override
    public <T, R> Parametrized<SocketF, R> map(Parametrized<SocketF, T> fa, Function<T, R> f) {
      return lift(fa).map(f);
    }
  };
}

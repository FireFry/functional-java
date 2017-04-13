package vlad.fp.free_example.banking;

import vlad.fp.lib.function.Function;
import vlad.fp.lib.higher.Functor;
import vlad.fp.lib.higher.Parametrized;

abstract class SocketF<T> implements Parametrized<SocketF, T> {
  static <T> SocketF<T> lift(Parametrized<SocketF, T> par) {
    return (SocketF<T>) par;
  }

  static final Functor<SocketF> FUNCTOR = new Functor<SocketF>() {
    @Override
    public <T, R> Parametrized<SocketF, R> map(Parametrized<SocketF, T> fa, Function<T, R> f) {
      return lift(fa).map(f);
    }
  };

  SocketF() {}

  abstract <R> SocketF<R> map(Function<T, R> f);

  abstract <R> R fold(Function<T, R> justReturnCase);

  static final class JustReturn<T> extends SocketF<T> {
    final T value;

    JustReturn(T value) {
      this.value = value;
    }

    @Override
    <R> JustReturn<R> map(Function<T, R> f) {
      return new JustReturn<>(f.apply(value));
    }

    @Override
    <R> R fold(Function<T, R> justReturnCase) {
      return justReturnCase.apply(value);
    }
  }
}

package vlad.fp.free_example.banking;

import vlad.fp.lib.function.Function;
import vlad.fp.lib.higher.Functor;
import vlad.fp.lib.higher.Parametrized;

abstract class ProtocolF<T> implements Parametrized<ProtocolF, T> {
  static <T> ProtocolF<T> lift(Parametrized<ProtocolF, T> par) {
    return (ProtocolF<T>) par;
  }

  static final Functor<ProtocolF> FUNCTOR = new Functor<ProtocolF>() {
    @Override
    public <T, R> Parametrized<ProtocolF, R> map(Parametrized<ProtocolF, T> fa, Function<T, R> f) {
      return lift(fa).map(f);
    }
  };

  private ProtocolF() {}

  abstract <R> ProtocolF<R> map(Function<T, R> f);

  abstract <R> R fold(Function<T, R> justReturnCase);

  static final class JustReturn<T> extends ProtocolF<T> {
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

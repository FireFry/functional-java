package vlad.fp.free_example.banking.socket;

import vlad.fp.lib.function.Function;

public final class JustReturn<T> extends SocketF<T> {
  public final T value;

  public JustReturn(T value) {
    this.value = value;
  }

  @Override
  public <R> JustReturn<R> map(Function<T, R> f) {
    return new JustReturn<>(f.apply(value));
  }

  @Override
  public <R> R fold(Function<T, R> justReturnCase) {
    return justReturnCase.apply(value);
  }
}

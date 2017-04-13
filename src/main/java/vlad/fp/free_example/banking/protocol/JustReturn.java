package vlad.fp.free_example.banking.protocol;

import vlad.fp.lib.function.Function;

public final class JustReturn<T> extends ProtocolF<T> {
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

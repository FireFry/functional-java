package vlad.fp.banking.dsl.protocol;

import vlad.fp.lib.Unit;
import vlad.fp.lib.function.Function;

public final class JustReturn<T> extends ProtocolF<T> {
  public final T value;
  public final Function<Unit, T> next;

  public JustReturn(T value, Function<Unit, T> next) {
    this.value = value;
    this.next = next;
  }

  @Override
  public <R> JustReturn<R> map(Function<T, R> f) {
    return new JustReturn<>(f.apply(value), x -> f.apply(next.apply(x)));
  }

  @Override
  public <R> R foldT(Function<JustReturn<T>, R> justReturnCase) {
    return justReturnCase.apply(this);
  }
}

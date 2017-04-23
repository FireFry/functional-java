package vlad.fp.maybe;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public final class Some<A> extends Maybe<A> {
  private final A value;

  public Some(A value) {
    this.value = Objects.requireNonNull(value);
  }

  public A value() {
    return value;
  }

  @Override
  public <B> B match(Function<None<A>, B> noneCase, Function<Some<A>, B> someCase) {
    return someCase.apply(this);
  }

  @Override
  public <B> B matchVal(Supplier<B> noneCase, Function<A, B> someCase) {
    return someCase.apply(value);
  }
}

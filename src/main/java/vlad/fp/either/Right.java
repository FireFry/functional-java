package vlad.fp.either;

import java.util.Objects;
import java.util.function.Function;

public final class Right<L, R> extends Either<L, R> {
  private final R value;

  public Right(R value) {
    this.value = Objects.requireNonNull(value);
  }

  public R value() {
    return value;
  }

  @Override
  public <B> B match(Function<Left<L, R>, B> leftCase, Function<Right<L, R>, B> rightCase) {
    return rightCase.apply(this);
  }

  @Override
  public <B> B matchVal(Function<L, B> leftCase, Function<R, B> rightCase) {
    return rightCase.apply(value);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Right<?, ?> right = (Right<?, ?>) o;

    return value.equals(right.value);
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }

  @Override
  public String toString() {
    return "Right(" + value + ")";
  }
}

package vlad.fp.either;

import java.util.Objects;
import java.util.function.Function;

public final class Left<L, R> extends Either<L, R> {
  private final L value;

  public Left(L value) {
    this.value = Objects.requireNonNull(value);
  }

  public L value() {
    return value;
  }

  @Override
  public <B> B match(Function<Left<L, R>, B> leftCase, Function<Right<L, R>, B> rightCase) {
    return leftCase.apply(this);
  }

  @Override
  public <B> B matchVal(Function<L, B> leftCase, Function<R, B> rightCase) {
    return leftCase.apply(value);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Left<?, ?> left = (Left<?, ?>) o;

    return value.equals(left.value);
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }

  @Override
  public String toString() {
    return "Left(" + value + ")";
  }
}

package vlad.fp.either;

import java.util.function.Function;

public final class Right<L, R> extends Either<L, R> {
  private final R value;

  public Right(R value) {
    this.value = value;
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
}

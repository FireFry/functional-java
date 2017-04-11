package vlad.fp.lib;

import vlad.fp.lib.function.Function;

public abstract class Either<L, R> {

  public static <L, R> Either<L, R> left(L value) {
    return Left(value);
  }

  public static <L, R> Either<L, R> right(R value) {
    return Right(value);
  }

  private Either() {
    // private constructor
  }

  public <T> Either<L, T> flatMap(Function<R, Either<L, T>> f) {
    return fold(Either::left, f);
  }

  public <T> Either<L, T> map(Function<R, T> f) {
    return fold(Either::left, x -> Either.right(f.apply(x)));
  }

  private enum Type {
    LEFT,
    RIGHT,
  }

  protected abstract Type getType();

  private <T> T foldT(
      Function<Left<L, R>, T> leftCase,
      Function<Right<L, R>, T> rightCase
  ) {
    switch (getType()) {
      case LEFT:
        return leftCase.apply(asLeft());
      case RIGHT:
        return rightCase.apply(asRight());
      default:
        throw new AssertionError();
    }
  }

  public <T> T fold(
      Function<L, T> leftCase,
      Function<R, T> rightCase
  ) {
    return foldT(
        left -> leftCase.apply(left.value),
        right -> rightCase.apply(right.value)
    );
  }

  private static final class Left<L, R> extends Either<L, R> {
    private final L value;

    private Left(L value) {
      this.value = value;
    }

    @Override
    protected Type getType() {
      return Type.LEFT;
    }

    @Override
    protected Left<L, R> asLeft() {
      return this;
    }
  }

  private static <L, R> Left<L, R> Left(L value) {
    return new Left<>(value);
  }

  protected Left<L, R> asLeft() {
    throw new AssertionError();
  }

  private static final class Right<L, R> extends Either<L, R> {
    private final R value;

    private Right(R value) {
      this.value = value;
    }

    @Override
    protected Type getType() {
      return Type.RIGHT;
    }

    @Override
    protected Right<L, R> asRight() {
      return this;
    }
  }

  private static <L, R> Right<L, R> Right(R value) {
    return new Right<>(value);
  }

  protected Right<L, R> asRight() {
    throw new AssertionError();
  }
}

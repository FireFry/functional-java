package vlad.fp.either;

import java.util.function.Function;

public abstract class Either<L, R> {

  public static <L, R> Left<L, R> left(L value) {
    return new Left<>(value);
  }

  public static <L, R> Right<L, R> right(R value) {
    return new Right<>(value);
  }

  Either() {

  }

  public abstract <B> B match(Function<Left<L, R>, B> leftCase, Function<Right<L, R>, B> rightCase);

  public abstract <B> B matchVal(Function<L, B> leftCase, Function<R, B> rightCase);

  public <T> Either<L, T> flatMap(Function<R, Either<L, T>> f) {
    return matchVal(Either::left, f);
  }

  public <T> Either<L, T> map(Function<R, T> f) {
    return matchVal(Either::left, x -> Either.right(f.apply(x)));
  }

  public <T> Either<T, R> mapLeft(Function<L, T> f) {
    return matchVal(x -> Either.left(f.apply(x)), Either::right);
  }

}

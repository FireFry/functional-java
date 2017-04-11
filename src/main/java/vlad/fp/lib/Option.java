package vlad.fp.lib;

import vlad.fp.lib.function.Function;
import vlad.fp.lib.function.Supplier;

public abstract class Option<T> {

  public static <T> Option<T> none() {
    return None();
  }

  public static <T> Option<T> some(T value) {
    return Some(value);
  }

  private enum Type {
    NONE,
    SOME,
  }

  private Option() {
    // private constructor
  }

  public T orElse(Supplier<T> elseCase) {
    return fold(elseCase, x -> x);
  }

  public <R> Option<R> flatMap(Function<T, Option<R>> f) {
    return fold(Option::none, f);
  }

  public <R> Option<R> map(Function<T, R> f) {
    return flatMap(x -> Option.some(f.apply(x)));
  }

  protected abstract Type getType();

  private <R> R foldT(
      Function<None<T>, R> noneCase,
      Function<Some<T>, R> someCase
  ) {
    switch (getType()) {
      case NONE:
        return noneCase.apply(asNone());
      case SOME:
        return someCase.apply(asSome());
      default:
        throw new AssertionError();
    }
  }

  public <R> R fold(
      Supplier<R> noneCase,
      Function<T, R> someCase
  ) {
    return foldT(
        none -> noneCase.apply(),
        some -> someCase.apply(some.value)
    );
  }

  private static final class None<T> extends Option<T> {
    private static final None instance = new None();

    @Override
    protected Type getType() {
      return Type.NONE;
    }

    @Override
    protected None<T> asNone() {
      return this;
    }
  }

  @SuppressWarnings("unchecked")
  private static <T> None<T> None() {
    return None.instance;
  }

  protected None<T> asNone() {
    throw new AssertionError();
  }

  private static final class Some<T> extends Option<T> {
    private final T value;

    private Some(T value) {
      this.value = value;
    }

    @Override
    protected Type getType() {
      return Type.SOME;
    }

    @Override
    protected Some<T> asSome() {
      return this;
    }
  }

  private static <T> Some<T> Some(T value) {
    return new Some<>(value);
  }

  protected Some<T> asSome() {
    throw new AssertionError();
  }
}

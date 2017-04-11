package vlad.fp.lib;

import vlad.fp.lib.function.Function;

public abstract class Tailrec<T, R> {

  public static <T, R> Tailrec<T, R> next(T value) {
    return Next(value);
  }

  public static <T, R> Tailrec<T, R> finish(R result) {
    return Finish(result);
  }

  private Tailrec() {
    // private constructor
  }

  public static <T, R> R run(T x, Function<T, Tailrec<T, R>> f) {
    Tailrec<T, R> tailrec = next(x);
    while (true) {
      switch (tailrec.getType()) {
        case FINISH:
          return tailrec.asFinish().result;
        case NEXT:
          tailrec = f.apply(tailrec.asNext().value);
          break;
        default:
          throw new AssertionError();
      }
    }
  }

  private enum Type {
    FINISH,
    NEXT,
  }

  protected abstract Type getType();

  private static final class Finish<T, R> extends Tailrec<T, R> {
    private final R result;

    private Finish(R result) {
      this.result = result;
    }

    @Override
    protected Type getType() {
      return Type.FINISH;
    }

    @Override
    protected Finish<T, R> asFinish() {
      return this;
    }
  }

  private static <T, R> Finish<T, R> Finish(R result) {
    return new Finish<>(result);
  }

  protected Finish<T, R> asFinish() {
    throw new AssertionError();
  }

  private static final class Next<T, R> extends Tailrec<T, R> {
    private final T value;

    private Next(T value) {
      this.value = value;
    }

    @Override
    protected Type getType() {
      return Type.NEXT;
    }

    @Override
    protected Next<T, R> asNext() {
      return this;
    }
  }

  private static <T, R> Next<T, R> Next(T value) {
    return new Next<>(value);
  }

  protected Next<T, R> asNext() {
    throw new AssertionError();
  }
}

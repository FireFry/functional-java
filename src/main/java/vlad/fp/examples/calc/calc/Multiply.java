package vlad.fp.examples.calc.calc;

public final class Multiply<T> extends Calc<T> {
  private final T first;
  private final T second;

  Multiply(T first, T second) {
    this.first = first;
    this.second = second;
  }

  public T first() {
    return first;
  }

  public T second() {
    return second;
  }
}

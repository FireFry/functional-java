package vlad.fp.examples.calc.calc;

public final class Add<T> extends Calc<T> {
  private final T first;
  private final T second;

  Add(T first, T second) {
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

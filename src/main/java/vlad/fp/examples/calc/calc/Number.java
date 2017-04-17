package vlad.fp.examples.calc.calc;

public final class Number<T> extends Calc<T> {
  private final Integer value;

  Number(Integer value) {
    this.value = value;
  }

  public Integer value() {
    return value;
  }
}

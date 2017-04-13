package vlad.fp.free_example.banking.model;

public final class Amount {
  public final int value;

  public Amount(int value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }
}

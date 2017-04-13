package vlad.fp.free_example.banking;

final class Amount {
  final int value;

  Amount(int value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }
}

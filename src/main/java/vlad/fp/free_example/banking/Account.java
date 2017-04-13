package vlad.fp.free_example.banking;

final class Account {
  final String id;

  Account(String id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return id;
  }
}

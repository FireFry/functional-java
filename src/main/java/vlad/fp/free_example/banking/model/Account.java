package vlad.fp.free_example.banking.model;

public final class Account {
  public final String id;

  public Account(String id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return id;
  }
}

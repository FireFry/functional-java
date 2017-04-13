package vlad.fp.banking.dsl.model;

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
package vlad.fp.examples.banking.dsl.model;

public final class From {
  public final Account account;

  public From(Account account) {
    this.account = account;
  }

  @Override
  public String toString() {
    return account.toString();
  }
}

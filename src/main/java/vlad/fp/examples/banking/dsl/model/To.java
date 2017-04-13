package vlad.fp.examples.banking.dsl.model;

public final class To {
  public final Account account;

  public To(Account account) {
    this.account = account;
  }

  @Override
  public String toString() {
    return account.toString();
  }
}

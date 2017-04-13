package vlad.fp.free_example.banking;

final class To {
  final Account account;

  To(Account account) {
    this.account = account;
  }

  @Override
  public String toString() {
    return account.toString();
  }
}

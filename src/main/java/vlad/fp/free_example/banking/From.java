package vlad.fp.free_example.banking;

final class From {
  final Account account;

  From(Account account) {
    this.account = account;
  }

  @Override
  public String toString() {
    return account.toString();
  }
}

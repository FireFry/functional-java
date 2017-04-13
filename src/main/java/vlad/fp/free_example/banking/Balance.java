package vlad.fp.free_example.banking;

import vlad.fp.lib.function.Function;

final class Balance<T> extends BankingF<T> {
  final Account account;
  final Function<Amount, T> next;

  Balance(Account account, Function<Amount, T> next) {
    this.account = account;
    this.next = next;
  }
}

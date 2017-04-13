package vlad.fp.free_example.banking;

import vlad.fp.lib.function.Function;

final class Withdraw<T> extends BankingF<T> {
  final Amount amount;
  final Function<Amount, T> next;

  Withdraw(Amount amount, Function<Amount, T> next) {
    this.amount = amount;
    this.next = next;
  }
}

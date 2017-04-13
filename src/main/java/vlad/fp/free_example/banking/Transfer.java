package vlad.fp.free_example.banking;

import vlad.fp.free_example.banking.model.Amount;
import vlad.fp.free_example.banking.model.From;
import vlad.fp.free_example.banking.model.To;
import vlad.fp.free_example.banking.model.TransferResult;
import vlad.fp.lib.function.Function;

final class Transfer<T> extends BankingF<T> {
  final Amount amount;
  final From from;
  final To to;
  final Function<TransferResult, T> next;

  Transfer(Amount amount, From from, To to, Function<TransferResult, T> next) {
    this.amount = amount;
    this.from = from;
    this.to = to;
    this.next = next;
  }
}

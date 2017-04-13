package vlad.fp.free_example.banking.banking;

import vlad.fp.free_example.banking.model.Amount;
import vlad.fp.free_example.banking.model.From;
import vlad.fp.free_example.banking.model.To;
import vlad.fp.free_example.banking.model.TransferResult;
import vlad.fp.lib.function.Function;

public final class Transfer<T> extends BankingF<T> {
  public final Amount amount;
  public final From from;
  public final To to;
  public final Function<TransferResult, T> next;

  public Transfer(Amount amount, From from, To to, Function<TransferResult, T> next) {
    this.amount = amount;
    this.from = from;
    this.to = to;
    this.next = next;
  }
}

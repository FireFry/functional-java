package vlad.fp.banking.dsl.banking;

import vlad.fp.banking.dsl.model.Amount;
import vlad.fp.banking.dsl.model.From;
import vlad.fp.banking.dsl.model.To;
import vlad.fp.banking.dsl.model.TransferResult;
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

package vlad.fp.banking.dsl.banking;

import vlad.fp.banking.dsl.model.Amount;
import vlad.fp.lib.function.Function;

public final class Withdraw<T> extends BankingF<T> {
  public final Amount amount;
  public final Function<Amount, T> next;

  public Withdraw(Amount amount, Function<Amount, T> next) {
    this.amount = amount;
    this.next = next;
  }
}
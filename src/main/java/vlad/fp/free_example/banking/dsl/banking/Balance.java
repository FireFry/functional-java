package vlad.fp.free_example.banking.dsl.banking;

import vlad.fp.free_example.banking.dsl.model.Account;
import vlad.fp.free_example.banking.dsl.model.Amount;
import vlad.fp.lib.function.Function;

public final class Balance<T> extends BankingF<T> {
  public final Account account;
  public final Function<Amount, T> next;

  public Balance(Account account, Function<Amount, T> next) {
    this.account = account;
    this.next = next;
  }
}
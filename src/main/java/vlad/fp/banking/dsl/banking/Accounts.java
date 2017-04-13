package vlad.fp.banking.dsl.banking;

import vlad.fp.banking.dsl.model.Account;
import vlad.fp.lib.function.Function;

import java.util.List;

public final class Accounts<T> extends BankingF<T> {
  public final Function<List<Account>, T> next;

  public Accounts(Function<List<Account>, T> next) {
    this.next = next;
  }
}

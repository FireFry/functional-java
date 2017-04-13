package vlad.fp.free_example.banking.dsl.banking;

import vlad.fp.free_example.banking.dsl.model.Account;
import vlad.fp.lib.function.Function;

import java.util.List;

public final class Accounts<T> extends BankingF<T> {
  public final Function<List<Account>, T> next;

  public Accounts(Function<List<Account>, T> next) {
    this.next = next;
  }
}

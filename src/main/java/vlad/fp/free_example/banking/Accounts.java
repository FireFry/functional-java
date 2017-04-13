package vlad.fp.free_example.banking;

import vlad.fp.lib.function.Function;

import java.util.List;

final class Accounts<T> extends BankingF<T> {
  final Function<List<Account>, T> next;

  Accounts(Function<List<Account>, T> next) {
    this.next = next;
  }
}

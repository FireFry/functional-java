package vlad.fp.free_example.banking;

import vlad.fp.free_example.banking.model.Account;
import vlad.fp.free_example.banking.model.Amount;
import vlad.fp.free_example.banking.model.From;
import vlad.fp.free_example.banking.model.To;
import vlad.fp.free_example.banking.model.TransferResult;
import vlad.fp.lib.higher.Parametrized;

import java.util.List;

interface Banking<F> extends Parametrized<Banking, F> {

  Parametrized<F, List<Account>> accounts();

  Parametrized<F, Amount> balance(Account account);

  Parametrized<F, TransferResult> transfer(Amount amount, From from, To to);

  Parametrized<F, Amount> withdraw(Amount amount);

}

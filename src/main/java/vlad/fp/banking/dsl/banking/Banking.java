package vlad.fp.banking.dsl.banking;

import vlad.fp.banking.dsl.model.Account;
import vlad.fp.banking.dsl.model.Amount;
import vlad.fp.banking.dsl.model.From;
import vlad.fp.banking.dsl.model.To;
import vlad.fp.banking.dsl.model.TransferResult;
import vlad.fp.lib.higher.Parametrized;

import java.util.List;

public interface Banking<F> extends Parametrized<Banking, F> {

  Parametrized<F, List<Account>> accounts();

  Parametrized<F, Amount> balance(Account account);

  Parametrized<F, TransferResult> transfer(Amount amount, From from, To to);

  Parametrized<F, Amount> withdraw(Amount amount);

}

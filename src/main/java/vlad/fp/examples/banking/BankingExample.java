package vlad.fp.examples.banking;

import static vlad.fp.examples.banking.dsl.model.ModelFactory.account;
import static vlad.fp.examples.banking.dsl.model.ModelFactory.amount;
import static vlad.fp.examples.banking.dsl.model.ModelFactory.from;
import static vlad.fp.examples.banking.dsl.model.ModelFactory.to;

import vlad.fp.examples.banking.dsl.banking.Banking;
import vlad.fp.examples.banking.dsl.banking.BankingF;
import vlad.fp.examples.banking.dsl.model.Amount;
import vlad.fp.examples.banking.exec.ExecBanking;
import vlad.fp.lib.Free;
import vlad.fp.lib.higher.Monad;
import vlad.fp.lib.Task;
import vlad.fp.lib.function.Function;
import vlad.fp.lib.higher.Parametrized;

public class BankingExample {
  private static <F> Parametrized<F, Amount> program(Monad<F> m, Banking<F> banking) {
    return m.flatMap(
        banking.accounts(), as -> m.flatMap(
        banking.balance(as.get(0)), b -> m.flatMap(
        banking.transfer(amount(123), from(account("Foo")), to(account("Bar"))), x -> m.map(
        banking.withdraw(amount(5)), Function.identity()))));
  }

  private static Free<BankingF, Amount> bankingFProgram() {
    return Free.lift(program(Free.monad(), BankingF.bankingFree(BankingF.FUNCTOR, BankingF.BANKING)));
  }

  public static void main(String[] args) {
    Task<Amount> task = Task.lift(bankingFProgram().foldMap(BankingF.FUNCTOR, Task.MONAD, ExecBanking.INSTANCE));
    Amount amount = task.run();
    System.out.println("Result: " + amount);
  }
}

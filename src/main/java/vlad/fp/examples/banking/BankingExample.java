package vlad.fp.examples.banking;

import vlad.fp.examples.banking.dsl.banking.Banking;
import vlad.fp.examples.banking.dsl.banking.BankingF;
import vlad.fp.examples.banking.dsl.model.Account;
import vlad.fp.examples.banking.dsl.model.Amount;
import vlad.fp.examples.banking.dsl.model.From;
import vlad.fp.examples.banking.dsl.model.To;
import vlad.fp.examples.banking.exec.ExecBanking;
import vlad.fp.lib.Free;
import vlad.fp.lib.Monad;
import vlad.fp.lib.Task;
import vlad.fp.lib.higher.Parametrized;

public class BankingExample {
  private static <F> Parametrized<F, Amount> program(Monad<F> m, Banking<F> banking) {
    return m.flatMap(
        banking.accounts(), as -> m.flatMap(
        banking.balance(as.get(0)), b -> m.flatMap(
        banking.transfer(new Amount(123), new From(new Account("Foo")), new To(new Account("Bar"))), x -> m.map(
        banking.withdraw(new Amount(5)), ignored -> b))));
  }

  private static Free<BankingF, Amount> bankingFProgram() {
    return Free.lift(program(Free.freeMonad(), BankingF.bankingFree(BankingF.FUNCTOR, BankingF.BANKING)));
  }

  public static void main(String[] args) {
    Task<Amount> task = Task.lift(bankingFProgram().foldMap(BankingF.FUNCTOR, Task.MONAD, ExecBanking.INSTANCE));
    Amount amount = task.run();
    System.out.println("Result: " + amount);
  }
}

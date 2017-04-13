package vlad.fp.banking;

import vlad.fp.banking.dsl.banking.Banking;
import vlad.fp.banking.dsl.banking.BankingF;
import vlad.fp.banking.dsl.model.Account;
import vlad.fp.banking.dsl.model.Amount;
import vlad.fp.banking.dsl.model.From;
import vlad.fp.banking.dsl.model.To;
import vlad.fp.banking.exec.ExecBanking;
import vlad.fp.lib.Free;
import vlad.fp.lib.Monad;
import vlad.fp.lib.Task;
import vlad.fp.lib.higher.Parametrized;

public class BankingExample {
  static <F> Parametrized<F, Amount> program(Monad<F> M, Banking<F> B) {
    return M.flatMap(
        B.accounts(), as -> M.flatMap(
        B.balance(as.get(0)), b -> M.flatMap(
        B.transfer(new Amount(123), new From(new Account("Foo")), new To(new Account("Bar"))), x -> M.map(
        B.withdraw(new Amount(5)), ignored -> b))));
  }

  static Free<BankingF, Amount> bankingFProgram() {
    return Free.lift(program(Free.freeMonad(), BankingF.bankingFree(BankingF.FUNCTOR, BankingF.BANKING)));
  }

  public static void main(String[] args) {
    Task<Amount> task = Task.lift(bankingFProgram().foldMap(BankingF.FUNCTOR, Task.MONAD, ExecBanking.INSTANCE));
    Amount amount = task.run();
    System.out.println("Result: " + amount);
  }
}

package vlad.fp.free_example.banking;

import vlad.fp.free_example.banking.model.Account;
import vlad.fp.free_example.banking.model.Amount;
import vlad.fp.free_example.banking.model.From;
import vlad.fp.free_example.banking.model.To;
import vlad.fp.lib.Free;
import vlad.fp.lib.Monad;
import vlad.fp.lib.Natural;
import vlad.fp.lib.Task;
import vlad.fp.lib.higher.Parametrized;

public class BankingExample {
  static final Natural<BankingF, Task> execBanking = new ExecBanking();

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
    Task<Amount> task = Task.lift(bankingFProgram().foldMap(BankingF.FUNCTOR, Task.MONAD, execBanking));
    Amount amount = task.run();
    System.out.println("Result: " + amount);
  }
}

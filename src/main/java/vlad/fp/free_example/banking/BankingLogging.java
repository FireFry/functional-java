package vlad.fp.free_example.banking;

import vlad.fp.lib.Free;
import vlad.fp.lib.higher.Parametrized;

class BankingLogging implements Interpreter<BankingF, Parametrized<Halt, LoggingF>> {
  static <T> Free<Parametrized<Halt, LoggingF>, T> log(String msg) {
    return Free.liftF(Halt.functor(), new Halt<>(new LoggingF.Log<>(msg)));
  }

  @Override
  public <T> Parametrized<Parametrized<Free, Parametrized<Halt, LoggingF>>, T> apply(Parametrized<BankingF, T> fa) {
    return BankingF.lift(fa).foldT(
        accounts -> log("Fetch accounts"),
        balance -> log("Fetch balance for account: " + balance.account),
        transfer -> log("Transfer " + transfer.amount + " from " + transfer.from + " to " + transfer.to),
        withdraw -> log("Withdraw " + withdraw.amount)
    );
  }
}
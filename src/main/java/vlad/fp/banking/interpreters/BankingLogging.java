package vlad.fp.banking.interpreters;

import vlad.fp.banking.dsl.banking.BankingF;
import vlad.fp.banking.dsl.halt.Halt;
import vlad.fp.banking.dsl.logging.Log;
import vlad.fp.banking.dsl.logging.LoggingF;
import vlad.fp.lib.Free;
import vlad.fp.lib.higher.Parametrized;

public enum BankingLogging implements Interpreter<BankingF, Parametrized<Halt, LoggingF>> {
  INSTANCE;

  private static <T> Free<Parametrized<Halt, LoggingF>, T> log(String msg) {
    return Free.liftF(Halt.functor(), new Halt<>(new Log<>(msg)));
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

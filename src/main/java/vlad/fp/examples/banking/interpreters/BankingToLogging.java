package vlad.fp.examples.banking.interpreters;

import vlad.fp.examples.banking.dsl.banking.BankingF;
import vlad.fp.examples.banking.dsl.halt.Halt;
import vlad.fp.examples.banking.dsl.logging.Log;
import vlad.fp.examples.banking.dsl.logging.LoggingF;
import vlad.fp.lib.Free;
import vlad.fp.lib.higher.Interpreter;
import vlad.fp.lib.function.Function;
import vlad.fp.lib.higher.Parametrized;

public enum BankingToLogging implements Interpreter<BankingF, Parametrized<Halt, LoggingF>> {
  INTERPRETER;

  private static <T> Free<Parametrized<Halt, LoggingF>, T> log(String msg) {
    return Free.liftF(new Halt<>(new Log<>(msg, Function.identity())));
  }

  @Override
  public <T> Free<Parametrized<Halt, LoggingF>, T> apply(Parametrized<BankingF, T> fa) {
    return BankingF.lift(fa).foldT(
        accounts -> log("Fetch accounts"),
        balance -> log("Fetch balance for account: " + balance.account),
        transfer -> log("Transfer " + transfer.amount + " from " + transfer.from + " to " + transfer.to),
        withdraw -> log("Withdraw " + withdraw.amount)
    );
  }
}

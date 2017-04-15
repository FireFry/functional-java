package vlad.fp.examples.banking.interpreters;

import static vlad.fp.examples.banking.dsl.model.ModelFactory.account;
import static vlad.fp.examples.banking.dsl.model.ModelFactory.amount;
import static vlad.fp.examples.banking.dsl.model.ModelFactory.error;

import com.google.common.collect.ImmutableList;
import vlad.fp.examples.banking.dsl.banking.BankingF;
import vlad.fp.examples.banking.dsl.model.TransferResult;
import vlad.fp.examples.banking.dsl.protocol.ProtocolF;
import vlad.fp.lib.Either;
import vlad.fp.lib.Free;
import vlad.fp.lib.Interpreter;
import vlad.fp.lib.higher.Parametrized;

public enum BankingToProtocol implements Interpreter<BankingF, ProtocolF> {
  INTERPRETER;

  @Override
  public <T> Free<ProtocolF, T> apply(Parametrized<BankingF, T> fa) {
    return BankingF.lift(fa).foldT(
        accounts -> ProtocolF.justReturn(ImmutableList.of(account("Foo"), account("Bar"))).map(accounts.next),
        balance -> ProtocolF.justReturn(amount(10000)).map(balance.next),
        transfer -> ProtocolF.justReturn(new TransferResult(Either.left(error("Ooops")))).map(transfer.next),
        withdraw -> ProtocolF.justReturn(amount(10000 - withdraw.amount.value)).map(withdraw.next)
    );
  }
}

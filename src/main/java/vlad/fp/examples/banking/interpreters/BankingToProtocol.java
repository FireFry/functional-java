package vlad.fp.examples.banking.interpreters;

import com.google.common.collect.ImmutableList;
import vlad.fp.examples.banking.dsl.banking.BankingF;
import vlad.fp.examples.banking.dsl.model.Account;
import vlad.fp.examples.banking.dsl.model.Amount;
import vlad.fp.examples.banking.dsl.model.Error;
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
        accounts -> ProtocolF.justReturn(accounts.next.apply(ImmutableList.of(new Account("Foo"), new Account("Bar")))),
        balance -> ProtocolF.justReturn(balance.next.apply(new Amount(10000))),
        transfer -> ProtocolF.justReturn(transfer.next.apply(new TransferResult(Either.left(new Error("Ooops"))))),
        withdraw -> ProtocolF.justReturn(withdraw.next.apply(new Amount(10000 - withdraw.amount.value)))
    );
  }
}

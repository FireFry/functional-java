package vlad.fp.banking.interpreters;

import com.google.common.collect.ImmutableList;
import vlad.fp.banking.dsl.banking.BankingF;
import vlad.fp.banking.dsl.model.Account;
import vlad.fp.banking.dsl.model.Amount;
import vlad.fp.banking.dsl.model.Error;
import vlad.fp.banking.dsl.model.TransferResult;
import vlad.fp.banking.dsl.protocol.JustReturn;
import vlad.fp.banking.dsl.protocol.ProtocolF;
import vlad.fp.lib.Either;
import vlad.fp.lib.Free;
import vlad.fp.lib.higher.Parametrized;

public enum BankingToProtocol implements Interpreter<BankingF, ProtocolF> {
  INSTANCE;

  @Override
  public <T> Parametrized<Parametrized<Free, ProtocolF>, T> apply(Parametrized<BankingF, T> fa) {
    return BankingF.lift(fa).foldT(
        accounts -> justReturn(accounts.next.apply(ImmutableList.of(new Account("Foo"), new Account("Bar")))),
        balance -> justReturn(balance.next.apply(new Amount(10000))),
        transfer -> justReturn(transfer.next.apply(new TransferResult(Either.left(new Error("Ooops"))))),
        withdraw -> justReturn(withdraw.next.apply(new Amount(10000 - withdraw.amount.value)))
    );
  }

  private static <T> Free<ProtocolF, T> justReturn(T apply) {
    return Free.liftF(ProtocolF.FUNCTOR, new JustReturn<>(apply));
  }
}

package vlad.fp.free_example.banking;

import com.google.common.collect.ImmutableList;
import vlad.fp.free_example.banking.dsl.banking.BankingF;
import vlad.fp.free_example.banking.dsl.model.Account;
import vlad.fp.free_example.banking.dsl.model.Amount;
import vlad.fp.free_example.banking.dsl.model.Error;
import vlad.fp.free_example.banking.dsl.model.TransferResult;
import vlad.fp.free_example.banking.dsl.protocol.JustReturn;
import vlad.fp.free_example.banking.dsl.protocol.ProtocolF;
import vlad.fp.lib.Either;
import vlad.fp.lib.Free;
import vlad.fp.lib.higher.Parametrized;

class BankingProtocol implements Interpreter<BankingF, ProtocolF> {
  @Override
  public <T> Parametrized<Parametrized<Free, ProtocolF>, T> apply(Parametrized<BankingF, T> fa) {
    return BankingF.lift(fa).foldT(
        accounts -> justReturn(accounts.next.apply(ImmutableList.of(new Account("Foo"), new Account("Bar")))),
        balance -> justReturn(balance.next.apply(new Amount(10000))),
        transfer -> justReturn(transfer.next.apply(new TransferResult(Either.left(new Error("Ooops"))))),
        withdraw -> justReturn(withdraw.next.apply(new Amount(10000 - withdraw.amount.value)))
    );
  }

  private <T> Free<ProtocolF, T> justReturn(T apply) {
    return Free.liftF(ProtocolF.FUNCTOR, new JustReturn<>(apply));
  }
}

package vlad.fp.examples.banking.dsl.banking;

import vlad.fp.examples.banking.dsl.model.Account;
import vlad.fp.examples.banking.dsl.model.Amount;
import vlad.fp.examples.banking.dsl.model.From;
import vlad.fp.examples.banking.dsl.model.To;
import vlad.fp.examples.banking.dsl.model.TransferResult;
import vlad.fp.lib.Free;
import vlad.fp.lib.function.Function;
import vlad.fp.lib.higher.Functor;
import vlad.fp.lib.higher.Parametrized;

import java.util.List;

public abstract class BankingF<T> implements Parametrized<BankingF, T> {
  public static <T> BankingF<T> lift(Parametrized<BankingF, T> par) {
    return (BankingF<T>) par;
  }

  BankingF() {}

  public <R> R foldT(
      Function<Accounts<T>, R> accountsCase,
      Function<Balance<T>, R> balanceCase,
      Function<Transfer<T>, R> transferCase,
      Function<Withdraw<T>, R> withdrawCase
  ) {
    Class<? extends BankingF> cls = getClass();
    if (cls.equals(Accounts.class)) {
      return accountsCase.apply((Accounts<T>) this);
    }
    if (cls.equals(Balance.class)) {
      return balanceCase.apply((Balance<T>) this);
    }
    if (cls.equals(Transfer.class)) {
      return transferCase.apply((Transfer<T>) this);
    }
    if (cls.equals(Withdraw.class)) {
      return withdrawCase.apply((Withdraw<T>) this);
    }
    throw new AssertionError();
  }

  public static final Banking<BankingF> BANKING = new Banking<BankingF>() {
    @Override
    public Parametrized<BankingF, List<Account>> accounts() {
      return new Accounts<>(Function.identity());
    }

    @Override
    public Parametrized<BankingF, Amount> balance(Account account) {
      return new Balance<>(account, Function.identity());
    }

    @Override
    public Parametrized<BankingF, TransferResult> transfer(Amount amount, From from, To to) {
      return new Transfer<>(amount, from, to, Function.identity());
    }

    @Override
    public Parametrized<BankingF, Amount> withdraw(Amount amount) {
      return new Withdraw<>(amount, Function.identity());
    }
  };

  public static final Functor<BankingF> FUNCTOR = new Functor<BankingF>() {
    @Override
    public <T, R> BankingF<R> map(Parametrized<BankingF, T> fa, Function<T, R> f) {
      return lift(fa).foldT(
          accounts -> new Accounts<>(list -> f.apply(accounts.next.apply(list))),
          balance -> new Balance<>(balance.account, amount -> f.apply(balance.next.apply(amount))),
          transfer -> new Transfer<>(transfer.amount, transfer.from, transfer.to, result -> f.apply(transfer.next.apply(result))),
          withdraw -> new Withdraw<>(withdraw.amount, amount -> f.apply(withdraw.next.apply(amount)))
      );
    }
  };

  public static <F> Banking<Parametrized<Free, F>> bankingFree(Functor<F> functor, Banking<F> banking) {
    return new Banking<Parametrized<Free, F>>() {
      @Override
      public Free<F, List<Account>> accounts() {
        return Free.liftF(functor, banking.accounts());
      }

      @Override
      public Free<F, Amount> balance(Account account) {
        return Free.liftF(functor, banking.balance(account));
      }

      @Override
      public Free<F, TransferResult> transfer(Amount amount, From from, To to) {
        return Free.liftF(functor, banking.transfer(amount, from, to));
      }

      @Override
      public Free<F, Amount> withdraw(Amount amount) {
        return Free.liftF(functor, banking.withdraw(amount));
      }
    };
  }
}

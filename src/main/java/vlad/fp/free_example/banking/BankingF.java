package vlad.fp.free_example.banking;

import vlad.fp.lib.Free;
import vlad.fp.lib.function.Function;
import vlad.fp.lib.higher.Functor;
import vlad.fp.lib.higher.Parametrized;

import java.util.List;

abstract class BankingF<T> implements Parametrized<BankingF, T> {
  static <T> BankingF<T> lift(Parametrized<BankingF, T> par) {
    return (BankingF<T>) par;
  }

  BankingF() {}

  <R> R foldT(
      Function<Accounts<T>, R> accountsCase,
      Function<Balance<T>, R> balanceCase,
      Function<Transfer<T>, R> transferCase,
      Function<Withdraw<T>, R> withdrawCase) {

    Class<? extends BankingF> cls = getClass();
    if (cls.equals(Accounts.class)) {
      return accountsCase.apply((Accounts<T>) this);
    } else if (cls.equals(Balance.class)) {
      return balanceCase.apply((Balance<T>) this);
    } else if (cls.equals(Transfer.class)) {
      return transferCase.apply((Transfer<T>) this);
    } else if (cls.equals(Withdraw.class)) {
      return withdrawCase.apply((Withdraw<T>) this);
    }
    throw new AssertionError();
  }

  static final Banking<BankingF> BANKING = new Banking<BankingF>() {
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

  static final Functor<BankingF> FUNCTOR = new Functor<BankingF>() {
    @Override
    public <T, R> Parametrized<BankingF, R> map(Parametrized<BankingF, T> fa, Function<T, R> f) {
      return lift(fa).foldT(
          accounts -> new Accounts<>(list -> f.apply(accounts.next.apply(list))),
          balance -> new Balance<>(balance.account, amount -> f.apply(balance.next.apply(amount))),
          transfer -> new Transfer<>(transfer.amount, transfer.from, transfer.to, result -> f.apply(transfer.next.apply(result))),
          withdraw -> new Withdraw<>(withdraw.amount, amount -> f.apply(withdraw.next.apply(amount)))
      );
    }
  };

  static <F> Banking<Parametrized<Free, F>> bankingFree(Functor<F> F, Banking<F> B) {
    return new Banking<Parametrized<Free, F>>() {
      @Override
      public Free<F, List<Account>> accounts() {
        return Free.liftF(F, B.accounts());
      }

      @Override
      public Free<F, Amount> balance(Account account) {
        return Free.liftF(F, B.balance(account));
      }

      @Override
      public Free<F, TransferResult> transfer(Amount amount, From from, To to) {
        return Free.liftF(F, B.transfer(amount, from, to));
      }

      @Override
      public Free<F, Amount> withdraw(Amount amount) {
        return Free.liftF(F, B.withdraw(amount));
      }
    };
  }
}

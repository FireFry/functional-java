package vlad.fp.free_example;

import vlad.fp.lib.Either;
import vlad.fp.lib.Free;
import vlad.fp.lib.Monad;
import vlad.fp.lib.Natural;
import vlad.fp.lib.function.Function;
import vlad.fp.lib.higher.Functor;
import vlad.fp.lib.higher.Parametrized;
import vlad.fp.lib.tuple.Tuple2;

import java.util.List;

public class BankingExample {

  static final class Amount {
    final int value;

    Amount(int value) {
      this.value = value;
    }
  }

  static final class Account {
    final String id;

    Account(String id) {
      this.id = id;
    }
  }

  static final class Error {
    final String reason;

    Error(String reason) {
      this.reason = reason;
    }
  }

  static final class From {
    final Account account;

    From(Account account) {
      this.account = account;
    }
  }

  static final class To {
    final Account account;

    To(Account account) {
      this.account = account;
    }
  }

  static final class TransferResult {
    final Either<Error, Tuple2<From, To>> result;

    TransferResult(Either<Error, Tuple2<From, To>> result) {
      this.result = result;
    }
  }

  interface Banking<F> extends Parametrized<Banking, F> {

    Parametrized<F, List<Account>> accounts();

    Parametrized<F, Amount> balance(Account account);

    Parametrized<F, TransferResult> transfer(Amount amount, From from, To to);

    Parametrized<F, Amount> withdraw(Amount amount);

  }

  abstract static class BankingF<T> implements Parametrized<BankingF, T> {
    static <T> BankingF<T> lift(Parametrized<BankingF, T> par) {
      return (BankingF<T>) par;
    }

    private BankingF() {}

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

  static final class Accounts<T> extends BankingF<T> {
    final Function<List<Account>, T> next;

    Accounts(Function<List<Account>, T> next) {
      this.next = next;
    }
  }

  static final class Balance<T> extends BankingF<T> {
    final Account account;
    final Function<Amount, T> next;

    Balance(Account account, Function<Amount, T> next) {
      this.account = account;
      this.next = next;
    }
  }

  static final class Transfer<T> extends BankingF<T> {
    final Amount amount;
    final From from;
    final To to;
    final Function<TransferResult, T> next;

    Transfer(Amount amount, From from, To to, Function<TransferResult, T> next) {
      this.amount = amount;
      this.from = from;
      this.to = to;
      this.next = next;
    }
  }

  static final class Withdraw<T> extends BankingF<T> {
    final Amount amount;
    final Function<Amount, T> next;

    Withdraw(Amount amount, Function<Amount, T> next) {
      this.amount = amount;
      this.next = next;
    }
  }

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

  static final class Halt<F, T> implements Parametrized<Parametrized<Halt, F>, T> {
    static <F, T> Halt<F, T> lift(Parametrized<Parametrized<Halt, F>, T> par) {
      return (Halt<F, T>) par;
    }

    final Parametrized<F, Void> p;

    Halt(Parametrized<F, Void> p) {
      this.p = p;
    }

    static <F> Functor<Parametrized<Halt, F>> functor() {
      return new Functor<Parametrized<Halt, F>>() {
        @Override
        public <T, R> Halt<F, R> map(Parametrized<Parametrized<Halt, F>, T> fa, Function<T, R> f) {
          return new Halt<>(Halt.lift(fa).p);
        }
      };
    }

    static class FreeHaltOps<F, T> {
      final Functor<F> functor;
      final Free<Parametrized<Halt, F>, T> free;

      FreeHaltOps(Functor<F> functor, Free<Parametrized<Halt, F>, T> free) {
        this.functor = functor;
        this.free = free;
      }

      Free<F, Void> unhalt() {
        return free.fold(functor(), arg -> Free.liftF(functor, Halt.lift(arg).p), arg -> Free.done(null));
      }
    }
  }

  static abstract class LoggingF<T> implements Parametrized<LoggingF, T> {
    private LoggingF() {}

    static final class Log extends LoggingF<Void> {
      final String msg;

      Log(String msg) {
        this.msg = msg;
      }
    }
  }

  static final Natural<BankingF, Parametrized<Free, Parametrized<Halt, LoggingF>>> bankingLogging = new Natural<BankingF, Parametrized<Free, Parametrized<Halt, LoggingF>>>() {
    <T> Free<Parametrized<Halt, LoggingF>, T> log(String msg) {
      return Free.liftF(Halt.functor(), new Halt<>(new LoggingF.Log(msg)));
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
  };

  public static void main(String[] args) {

  }

}

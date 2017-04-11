package vlad.fp.free_example;

import vlad.fp.lib.Either;
import vlad.fp.lib.Free;
import vlad.fp.lib.Monad;
import vlad.fp.lib.function.Function;
import vlad.fp.lib.functor.Functor;
import vlad.fp.lib.generic.Generic;
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

  interface Banking<F> {

    Generic<F, List<Account>> accounts();

    Generic<F, Amount> balance(Account account);

    Generic<F, TransferResult> transfer(Amount amount, From from, To to);

    Generic<F, Amount> withdraw(Amount amount);

  }

  abstract static class BankingF<T> implements Generic<BankingF, T> {
    private BankingF() {}

    static final Banking<BankingF> BANKING = new Banking<BankingF>() {
      @Override
      public Generic<BankingF, List<Account>> accounts() {
        return new Accounts<>(Function.identity());
      }

      @Override
      public Generic<BankingF, Amount> balance(Account account) {
        return new Balance<>(account, Function.identity());
      }

      @Override
      public Generic<BankingF, TransferResult> transfer(Amount amount, From from, To to) {
        return new Transfer<>(amount, from, to, Function.identity());
      }

      @Override
      public Generic<BankingF, Amount> withdraw(Amount amount) {
        return new Withdraw<>(amount, Function.identity());
      }
    };

    static <F> Banking<Generic<Free, F>> bankingFree(Functor<F> F, Banking<F> B) {
      return new Banking<Generic<Free, F>>() {
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

  static <F> Generic<F, Amount> program(Monad<F> M, Banking<F> B) {
    return M.flatMap(B.accounts(), as -> M.flatMap(
        B.balance(as.get(0)), b -> M.flatMap(
        B.transfer(new Amount(123), new From(new Account("Foo")), new To(new Account("Bar"))), x -> M.map(
        B.withdraw(new Amount(5)), ignored -> b))));
  }

}

package vlad.fp.free_example.banking;

import vlad.fp.lib.function.Function;
import vlad.fp.lib.higher.Functor;
import vlad.fp.lib.higher.Parametrized;

abstract class LoggingF<T> implements Parametrized<LoggingF, T> {
  LoggingF() {}

  static <T> LoggingF<T> lift(Parametrized<LoggingF, T> par) {
    return (LoggingF<T>) par;
  }

  <R> R foldT(Function<Log, R> logCase) {
    return logCase.apply((Log) this);
  }

  static final Functor<LoggingF> FUNCTOR = new Functor<LoggingF>() {
    @Override
    public <T, R> Parametrized<LoggingF, R> map(Parametrized<LoggingF, T> fa, Function<T, R> f) {
      return lift(fa).map(f);
    }
  };

  protected abstract <R> LoggingF<R> map(Function<T, R> f);

  static final class Log<T> extends LoggingF<T> {
    final String msg;

    Log(String msg) {
      this.msg = msg;
    }

    @Override
    protected <R> LoggingF<R> map(Function<T, R> f) {
      return new Log<>(msg);
    }
  }
}

package vlad.fp.free_example.banking.dsl.logging;

import vlad.fp.lib.function.Function;
import vlad.fp.lib.higher.Functor;
import vlad.fp.lib.higher.Parametrized;

public abstract class LoggingF<T> implements Parametrized<LoggingF, T> {
  LoggingF() {
    // sealed
  }

  public static <T> LoggingF<T> lift(Parametrized<LoggingF, T> par) {
    return (LoggingF<T>) par;
  }

  public <R> R foldT(Function<Log, R> logCase) {
    return logCase.apply((Log) this);
  }

  public abstract <R> LoggingF<R> map(Function<T, R> f);

  public static final Functor<LoggingF> FUNCTOR = new Functor<LoggingF>() {
    @Override
    public <T, R> Parametrized<LoggingF, R> map(Parametrized<LoggingF, T> fa, Function<T, R> f) {
      return lift(fa).map(f);
    }
  };
}

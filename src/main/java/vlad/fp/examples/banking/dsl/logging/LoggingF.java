package vlad.fp.examples.banking.dsl.logging;

import vlad.fp.lib.function.Function;
import vlad.fp.lib.higher.Functor;
import vlad.fp.lib.higher.Parametrized;

public abstract class LoggingF<T> implements Parametrized<LoggingF, T> {
  public static <T> LoggingF<T> lift(Parametrized<LoggingF, T> par) {
    return (LoggingF<T>) par;
  }

  LoggingF() {}

  public <R> R foldT(Function<Log<T>, R> logCase) {
    return logCase.apply((Log<T>) this);
  }

  public abstract <R> LoggingF<R> map(Function<T, R> f);

  public static final Functor<LoggingF> FUNCTOR = new Functor<LoggingF>() {
    @Override
    public <T, R> LoggingF<R> map(Parametrized<LoggingF, T> fa, Function<T, R> f) {
      return lift(fa).map(f);
    }
  };
}

package vlad.fp.examples.banking.dsl.logging;

import vlad.fp.lib.Unit;
import vlad.fp.lib.function.Function;

public final class Log<T> extends LoggingF<T> {
  public final String msg;
  public final Function<Unit, T> next;

  public Log(String msg, Function<Unit, T> next) {
    this.msg = msg;
    this.next = next;
  }

  @Override
  public <R> LoggingF<R> map(Function<T, R> f) {
    return new Log<>(msg, x -> f.apply(next.apply(x)));
  }
}

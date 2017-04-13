package vlad.fp.free_example.banking.dsl.logging;

import vlad.fp.lib.function.Function;

public final class Log<T> extends LoggingF<T> {
  public final String msg;

  public Log(String msg) {
    this.msg = msg;
  }

  @Override
  public <R> LoggingF<R> map(Function<T, R> f) {
    return new Log<>(msg);
  }
}

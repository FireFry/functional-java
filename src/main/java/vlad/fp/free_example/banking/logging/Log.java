package vlad.fp.free_example.banking.logging;

import vlad.fp.lib.function.Function;

public final class Log<T> extends LoggingF<T> {
  public final String msg;

  public Log(String msg) {
    this.msg = msg;
  }

  @Override
  public <R> LoggingF<R> map(Function<T, R> f) {
    return new vlad.fp.free_example.banking.logging.Log<>(msg);
  }
}

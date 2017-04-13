package vlad.fp.examples.console.dsl.console;

import vlad.fp.lib.function.Function;

public final class ReadLine<T> extends ConsoleF<T> {
  public final Function<String, T> next;

  public ReadLine(Function<String, T> next) {
    this.next = next;
  }

  @Override
  public <R> ReadLine<R> map(Function<T, R> f) {
    return new ReadLine<>(x -> f.apply(next.apply(x)));
  }
}

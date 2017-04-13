package vlad.fp.examples.console.dsl.console;

import vlad.fp.lib.function.Function;

public final class WriteLine<T> extends ConsoleF<T> {
  public final String s;
  public final Function<String, T> next;

  public WriteLine(String s, Function<String, T> next) {
    this.s = s;
    this.next = next;
  }

  @Override
  public <R> WriteLine<R> map(Function<T, R> f) {
    return new WriteLine<>(s, x -> f.apply(next.apply(x)));
  }
}

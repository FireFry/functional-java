package vlad.fp.banking.dsl.file;

import vlad.fp.lib.Unit;
import vlad.fp.lib.function.Function;

public final class AppendToFile<T> extends FileF<T> {
  public final String fileName;
  public final String string;
  public final Function<Unit, T> next;

  public AppendToFile(String fileName, String string, Function<Unit, T> next) {
    this.fileName = fileName;
    this.string = string;
    this.next = next;
  }

  @Override
  public <R> FileF<R> map(Function<T, R> f) {
    return new AppendToFile<>(fileName, string, x -> f.apply(next.apply(x)));
  }
}

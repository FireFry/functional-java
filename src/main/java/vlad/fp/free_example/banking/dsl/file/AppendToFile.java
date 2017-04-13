package vlad.fp.free_example.banking.dsl.file;

import vlad.fp.lib.function.Function;

public final class AppendToFile<T> extends FileF<T> {
  public final String fileName;
  public final String string;

  public AppendToFile(String fileName, String string) {
    this.fileName = fileName;
    this.string = string;
  }

  @Override
  public <R> FileF<R> map(Function<T, R> f) {
    return new AppendToFile<>(fileName, string);
  }
}

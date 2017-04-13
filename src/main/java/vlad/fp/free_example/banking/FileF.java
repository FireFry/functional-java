package vlad.fp.free_example.banking;

import vlad.fp.lib.function.Function;
import vlad.fp.lib.higher.Functor;
import vlad.fp.lib.higher.Parametrized;

abstract class FileF<T> implements Parametrized<FileF, T> {
  static <T> FileF<T> lift(Parametrized<FileF, T> par) {
    return (FileF<T>) par;
  }

  FileF() {}

  <R> R foldT(Function<AppendToFile<T>, R> appendCase) {
    return appendCase.apply((AppendToFile<T>) this);
  }

  abstract <R> FileF<R> map(Function<T, R> f);

  static Functor<FileF> FUNCTOR = new Functor<FileF>() {
    @Override
    public <T, R> Parametrized<FileF, R> map(Parametrized<FileF, T> fa, Function<T, R> f) {
      return lift(fa).map(f);
    }
  };

  static final class AppendToFile<T> extends FileF<T> {
    final String fileName;
    final String string;

    AppendToFile(String fileName, String string) {
      this.fileName = fileName;
      this.string = string;
    }

    @Override
    <R> FileF<R> map(Function<T, R> f) {
      return new AppendToFile<>(fileName, string);
    }
  }
}

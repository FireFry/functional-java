package vlad.fp.banking.dsl.file;

import vlad.fp.lib.function.Function;
import vlad.fp.lib.higher.Functor;
import vlad.fp.lib.higher.Parametrized;

public abstract class FileF<T> implements Parametrized<FileF, T> {
  public static <T> FileF<T> lift(Parametrized<FileF, T> par) {
    return (FileF<T>) par;
  }

  FileF() {}

  public <R> R foldT(Function<AppendToFile<T>, R> appendCase) {
    return appendCase.apply((AppendToFile<T>) this);
  }

  public abstract <R> FileF<R> map(Function<T, R> f);

  public static Functor<FileF> FUNCTOR = new Functor<FileF>() {
    @Override
    public <T, R> Parametrized<FileF, R> map(Parametrized<FileF, T> fa, Function<T, R> f) {
      return lift(fa).map(f);
    }
  };
}

package vlad.fp.examples.console.dsl.console;

import vlad.fp.lib.Free;
import vlad.fp.lib.Trampoline;
import vlad.fp.lib.function.Function;
import vlad.fp.lib.higher.Functor;
import vlad.fp.lib.higher.Parametrized;

public abstract class ConsoleF<T> implements Parametrized<ConsoleF, T> {
  public static <T> ConsoleF<T> lift(Parametrized<ConsoleF, T> par) {
    return (ConsoleF<T>) par;
  }

  ConsoleF() {}

  public abstract <R> ConsoleF<R> map(Function<T, R> f);

  public static Console<ConsoleF> CONSOLE = new Console<ConsoleF>() {
    @Override
    public Parametrized<ConsoleF, String> readLine() {
      return new ReadLine<>(Function.identity());
    }

    @Override
    public Parametrized<ConsoleF, String> writeLine(String s) {
      return new WriteLine<>(s, Function.identity());
    }
  };

  public static Functor<ConsoleF> FUNCTOR = new Functor<ConsoleF>() {
    @Override
    public <T, R> Parametrized<ConsoleF, R> map(Parametrized<ConsoleF, T> fa, Function<T, R> f) {
      return lift(fa).map(f);
    }
  };

  public static <F> Console<Parametrized<Free, F>> consoleFree(Functor<F> functor, Console<F> console) {
    return new Console<Parametrized<Free,F>>() {
      @Override
      public Parametrized<Parametrized<Free, F>, String> readLine() {
        return Free.liftF(functor, console.readLine());
      }

      @Override
      public Parametrized<Parametrized<Free, F>, String> writeLine(String s) {
        return Free.liftF(functor, console.writeLine(s));
      }
    };
  }

  public <R> R foldT(
      Function<ReadLine<T>, R> readLineCase,
      Function<WriteLine<T>, R> writeLineCase
  ) {
    Class<? extends ConsoleF> cls = getClass();
    if (cls.equals(ReadLine.class)) {
      return readLineCase.apply((ReadLine<T>) this);
    }
    if (cls.equals(WriteLine.class)) {
      return writeLineCase.apply((WriteLine<T>) this);
    }
    throw new AssertionError();
  }
}

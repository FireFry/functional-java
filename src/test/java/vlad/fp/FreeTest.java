package vlad.fp;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import vlad.fp.free.Free;
import vlad.fp.higher.Functor;
import vlad.fp.higher.Monad;
import vlad.fp.higher.Natural;
import vlad.fp.higher.Parametrized;
import vlad.fp.utils.Unchecked;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.function.Function;

@RunWith(MockitoJUnitRunner.class)
public class FreeTest {

  @Mock
  PrintWriter printWriter;

  @Mock
  BufferedReader bufferedReader;

  private static <F> Parametrized<F, String> program(Monad<F> m, Console<F> console) {
    return m.flatMap(
        console.writeLine("Enter the first line:"), u1 -> m.flatMap(
        console.readLine(), firstLine -> m.flatMap(
        console.writeLine("Enter the second line:"), u2 -> m.flatMap(
        console.readLine(), secondLine -> m.map(
        console.writeLine("You entered: \"" + firstLine + "\" and \"" + secondLine + "\""), Function.identity())))));
  }

  private static Free<ConsoleF, String> consoleFProgram() {
    return Free.lift(program(Free.monad(), ConsoleF.consoleFree(ConsoleF.CONSOLE)));
  }

  @Test
  public void test() throws IOException {
    when(bufferedReader.readLine())
        .thenReturn("foo")
        .thenReturn("bar");

    ConsoleIO consoleIO = new ConsoleIO(printWriter, bufferedReader);
    Trampoline<String> trampoline = Trampoline.lift(consoleFProgram().foldMap(ConsoleF.FUNCTOR, Trampoline.monad(), consoleIO));
    trampoline.run();

    verify(printWriter).println("Enter the first line:");
    verify(printWriter).println("Enter the second line:");
    verify(printWriter).println("You entered: \"foo\" and \"bar\"");
  }

  public static final class ConsoleIO implements Natural<ConsoleF, Trampoline> {
    private final PrintWriter printWriter;
    private final BufferedReader bufferedReader;

    public ConsoleIO(PrintWriter printWriter, BufferedReader bufferedReader) {
      this.printWriter = printWriter;
      this.bufferedReader = bufferedReader;
    }

    @Override
    public <T> Parametrized<Trampoline, T> apply(Parametrized<ConsoleF, T> fa) {
      return ConsoleF.lift(fa).foldT(
          readLine -> Trampoline.delay(Unchecked.get(bufferedReader::readLine)).map(readLine.next),
          writeLine -> Trampoline.delay(() -> {
            printWriter.println(writeLine.s);
            printWriter.flush();
            return writeLine.s;
          }).map(writeLine.next)
      );
    }
  }

  public static interface Console<F> extends Parametrized<Console, F> {

    Parametrized<F, String> readLine();

    Parametrized<F, String> writeLine(String s);

  }

  public abstract static class ConsoleF<T> implements Parametrized<ConsoleF, T> {
    public static <T> ConsoleF<T> lift(Parametrized<ConsoleF, T> par) {
      return (ConsoleF<T>) par;
    }

    ConsoleF() {}

    public abstract <R> ConsoleF<R> map(Function<T, R> f);

    public static Console<ConsoleF>
        CONSOLE = new Console<ConsoleF>() {
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

    public static <F> Console<Parametrized<Free, F>> consoleFree(Console<F> console) {
      return new Console<Parametrized<Free,F>>() {
        @Override
        public Parametrized<Parametrized<Free, F>, String> readLine() {
          return Free.liftF(console.readLine());
        }

        @Override
        public Parametrized<Parametrized<Free, F>, String> writeLine(String s) {
          return Free.liftF(console.writeLine(s));
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

  public static final class ReadLine<T> extends ConsoleF<T> {
    public final Function<String, T> next;

    public ReadLine(Function<String, T> next) {
      this.next = next;
    }

    @Override
    public <R> ReadLine<R> map(Function<T, R> f) {
      return new ReadLine<>(x -> f.apply(next.apply(x)));
    }
  }

  public static final class WriteLine<T> extends ConsoleF<T> {
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
}

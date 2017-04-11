package vlad.fp.lib;

import com.google.common.base.Throwables;
import vlad.fp.lib.function.Function;
import vlad.fp.lib.function.Supplier;

import java.util.function.Consumer;

public abstract class Utils {
  private Utils() {
    // non-instantiatable class
  }

  public static Void voidOf(Runnable runnable) {
    runnable.run();
    return null;
  }

  public static Supplier<Void> voidS(Runnable runnable) {
    return () -> {
      runnable.run();
      return null;
    };
  }

  public static <T> Function<T, Void> voidF(Consumer<T> consumer) {
    return o -> {
      consumer.accept(o);
      return null;
    };
  }

  public static Void unmatched() {
    throw new AssertionError();
  }

  public static Runnable logErrors(Runnable runnable) {
    return () -> {
      try {
        runnable.run();
      } catch (Throwable t) {
        t.printStackTrace();
        throw t;
      }
    };
  }

  public static <T> T propagate(Throwable th) {
    Throwables.throwIfUnchecked(th);
    throw new RuntimeException(th);
  }
}

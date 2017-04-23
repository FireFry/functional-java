package vlad.fp;

import java.util.function.Consumer;
import java.util.function.Function;

public enum Unit {
  UNIT;

  public static Unit run(Runnable runnable) {
    runnable.run();
    return UNIT;
  }

  public static <T> Function<T, Unit> accept(Consumer<T> consumer) {
    return o -> {
      consumer.accept(o);
      return UNIT;
    };
  }
}

package vlad.fp.lib;

import vlad.fp.lib.function.Function;

import java.util.function.Consumer;

public enum Unit {
  UNIT;

  public static Unit of(Runnable runnable) {
    runnable.run();
    return null;
  }

  public static <T> Function<T, Unit> of(Consumer<T> consumer) {
    return o -> {
      consumer.accept(o);
      return null;
    };
  }
}

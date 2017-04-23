package vlad.fp.utils;

import com.google.common.base.Throwables;

import java.util.function.Supplier;

public class Unchecked {

  public static <T> Supplier<T> get(UncheckedSupplier<T> unchecked) {
    return () -> {
      try {
        return unchecked.get();
      } catch (Exception e) {
        Throwables.throwIfUnchecked(e);
        throw new RuntimeException(e);
      }
    };
  }

  public interface UncheckedSupplier<T> {
    T get() throws Exception;
  }

  public static <T> T propagate(Throwable th) {
    Throwables.throwIfUnchecked(th);
    throw new RuntimeException(th);
  }

  private Unchecked() {

  }

}

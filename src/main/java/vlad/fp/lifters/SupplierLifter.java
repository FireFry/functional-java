package vlad.fp.lifters;

import vlad.fp.higher.Parametrized;

import java.util.Objects;
import java.util.function.Supplier;

public class SupplierLifter {

  public static <A> Supplier<A> lift(Parametrized<Supplier, A> par) {
    return ((SupplierWrapper<A>) par).supplier;
  }

  public static <A> Parametrized<Supplier, A> unlift(Supplier<A> supplier) {
    return new SupplierWrapper<>(supplier);
  }

  private SupplierLifter() {

  }

  private static final class SupplierWrapper<A> implements Parametrized<Supplier,A> {
    private final Supplier<A> supplier;

    private SupplierWrapper(Supplier<A> supplier) {
      this.supplier = Objects.requireNonNull(supplier);
    }
  }

}

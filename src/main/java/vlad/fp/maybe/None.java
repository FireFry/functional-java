package vlad.fp.maybe;

import java.util.function.Function;
import java.util.function.Supplier;

public final class None<A> extends Maybe<A> {
  private static final None INSTANCE = new None();

  private None() {

  }

  @SuppressWarnings("unchecked")
  public static <A> None<A> get() {
    return INSTANCE;
  }

  @Override
  public <B> B match(Function<None<A>, B> noneCase, Function<Some<A>, B> someCase) {
    return noneCase.apply(this);
  }

  @Override
  public <B> B matchVal(Supplier<B> noneCase, Function<A, B> someCase) {
    return noneCase.get();
  }
}

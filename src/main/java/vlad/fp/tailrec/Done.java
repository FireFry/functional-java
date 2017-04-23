package vlad.fp.tailrec;

import java.util.function.Function;

public final class Done<A> extends TailRec<A> {
  private final A value;

  public Done(A value) {
    this.value = value;
  }

  public A value() {
    return value;
  }

  @Override
  public <B> B match(Function<Done<A>, B> doneCase, Function<Suspend<A>, B> suspendCase) {
    return doneCase.apply(this);
  }
}

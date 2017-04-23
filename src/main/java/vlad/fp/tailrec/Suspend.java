package vlad.fp.tailrec;

import java.util.function.Function;
import java.util.function.Supplier;

public final class Suspend<A> extends TailRec<A> {
  private final Supplier<TailRec<A>> next;

  public Suspend(Supplier<TailRec<A>> next) {
    this.next = next;
  }

  public Supplier<TailRec<A>> next() {
    return next;
  }

  @Override
  public <B> B match(Function<Done<A>, B> doneCase, Function<Suspend<A>, B> suspendCase) {
    return suspendCase.apply(this);
  }
}

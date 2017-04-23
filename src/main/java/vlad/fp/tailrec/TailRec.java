package vlad.fp.tailrec;

import java.util.function.Function;
import java.util.function.Supplier;

public abstract class TailRec<A> {

  public static <A> Done<A> done(A value) {
    return new Done<>(value);
  }

  public static <A> Suspend<A> suspend(Supplier<TailRec<A>> supplier) {
    return new Suspend<>(supplier);
  }

  TailRec() {

  }

  public abstract <B> B match(Function<Done<A>, B> doneCase, Function<Suspend<A>, B> suspendCase);

  public A eval() {
    TailRec<A> next = this;
    while (next.match(done -> false, suspend -> true)) {
      next = next.match(done -> done, suspend -> suspend.next().get());
    }
    return next.match(Done::value, suspend -> { throw new AssertionError(); });
  }

}

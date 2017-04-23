package vlad.fp.future;

import java.util.function.Function;
import java.util.function.Supplier;

public final class Suspend<A> extends Future<A> {
  private final Supplier<Future<A>> value;

  public Suspend(Supplier<Future<A>> value) {
    this.value = value;
  }

  public Supplier<Future<A>> value() {
    return value;
  }

  @Override
  public <B, S> B match(
      Function<Now<A>, B> nowCase,
      Function<Suspend<A>, B> suspendCase,
      Function<Async<A>, B> asyncCase,
      Function<BindSuspend<S, A>, B> bindSuspendCase,
      Function<BindAsync<S, A>, B> bindAsyncCase) {
    return suspendCase.apply(this);
  }
}

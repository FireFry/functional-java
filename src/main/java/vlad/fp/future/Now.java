package vlad.fp.future;

import java.util.function.Function;

public final class Now<A> extends Future<A> {
  private final A value;

  public Now(A value) {
    this.value = value;
  }

  public A value() {
    return value;
  }

  @Override
  public <B, S> B match(
      Function<Now<A>, B> nowCase,
      Function<Suspend<A>, B> suspendCase,
      Function<Async<A>, B> asyncCase,
      Function<BindSuspend<S, A>, B> bindSuspendCase,
      Function<BindAsync<S, A>, B> bindAsyncCase) {
    return nowCase.apply(this);
  }
}

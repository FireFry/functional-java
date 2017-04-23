package vlad.fp.future;

import java.util.function.Function;
import java.util.function.Supplier;

public final class BindSuspend<S, A> extends Future<A> {
  private final Supplier<Future<S>> prev;
  private final Function<S, Future<A>> function;

  public BindSuspend(Supplier<Future<S>> prev, Function<S, Future<A>> function) {
    this.prev = prev;
    this.function = function;
  }

  public Supplier<Future<S>> prev() {
    return prev;
  }

  public Function<S, Future<A>> function() {
    return function;
  }

  @Override
  public <B, S> B match(
      Function<Now<A>, B> nowCase,
      Function<Suspend<A>, B> suspendCase,
      Function<Async<A>, B> asyncCase,
      Function<BindSuspend<S, A>, B> bindSuspendCase,
      Function<BindAsync<S, A>, B> bindAsyncCase) {
    //noinspection unchecked
    return bindSuspendCase.apply((BindSuspend<S, A>) this);
  }
}

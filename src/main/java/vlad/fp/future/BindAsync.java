package vlad.fp.future;

import vlad.fp.Trampoline;
import vlad.fp.Unit;

import java.util.function.Function;

public final class BindAsync<S, A> extends Future<A> {
  private final Function<Function<S, Trampoline<Unit>>, Unit> listener;
  private final Function<S, Future<A>> function;

  public BindAsync(Function<Function<S, Trampoline<Unit>>, Unit> listener, Function<S, Future<A>> function) {
    this.listener = listener;
    this.function = function;
  }

  public Function<Function<S, Trampoline<Unit>>, Unit> listener() {
    return listener;
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
    return bindAsyncCase.apply((BindAsync<S, A>) this);
  }
}

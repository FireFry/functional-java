package vlad.fp.future;

import vlad.fp.Trampoline;
import vlad.fp.Unit;

import java.util.function.Function;

public final class Async<A> extends Future<A> {
  private final Function<Function<A, Trampoline<Unit>>, Unit> listener;

  public Async(Function<Function<A, Trampoline<Unit>>, Unit> listener) {
    this.listener = listener;
  }

  public Function<Function<A, Trampoline<Unit>>, Unit> listener() {
    return listener;
  }

  @Override
  public <B, S> B match(
      Function<Now<A>, B> nowCase,
      Function<Suspend<A>, B> suspendCase,
      Function<Async<A>, B> asyncCase,
      Function<BindSuspend<S, A>, B> bindSuspendCase,
      Function<BindAsync<S, A>, B> bindAsyncCase) {
    return asyncCase.apply(this);
  }
}

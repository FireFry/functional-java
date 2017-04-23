package vlad.fp.free;

import java.util.function.Function;

public final class BindSuspend<F, S, A> extends Free<F, A> {
  private final Free<F, S> prev;
  private final Function<S, Free<F, A>> function;

  public BindSuspend(Free<F, S> prev, Function<S, Free<F, A>> function) {
    this.prev = prev;
    this.function = function;
  }

  public Free<F, S> prev() {
    return prev;
  }

  public Function<S, Free<F, A>> function() {
    return function;
  }

  @Override
  public <B> Free<F, B> flatMap(Function<A, Free<F, B>> f) {
    return new BindSuspend<>(prev, s -> new BindSuspend<>(function.apply(s), f));
  }

  @Override
  public <S, B> B match(
      Function<Done<F, A>, B> doneCase,
      Function<Suspend<F, A>, B> suspendCase,
      Function<BindSuspend<F, S, A>, B> bindSuspendCase) {
    //noinspection unchecked
    return bindSuspendCase.apply((BindSuspend<F, S, A>) this);
  }
}

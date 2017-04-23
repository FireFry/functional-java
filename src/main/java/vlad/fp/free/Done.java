package vlad.fp.free;

import java.util.function.Function;

public final class Done<F, A> extends Free<F, A> {
  private final A value;

  public Done(A value) {
    this.value = value;
  }

  public A value() {
    return value;
  }

  @Override
  public <S, B> B match(
      Function<Done<F, A>, B> doneCase,
      Function<Suspend<F, A>, B> suspendCase,
      Function<BindSuspend<F, S, A>, B> bindSuspendCase) {
    return doneCase.apply(this);
  }
}

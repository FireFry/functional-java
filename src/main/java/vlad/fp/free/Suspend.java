package vlad.fp.free;

import vlad.fp.higher.Parametrized;

import java.util.function.Function;

public final class Suspend<F, A> extends Free<F, A> {
  private final Parametrized<F, A> value;

  public Suspend(Parametrized<F, A> value) {
    this.value = value;
  }

  public Parametrized<F, A> value() {
    return value;
  }

  @Override
  public <S, B> B match(
      Function<Done<F, A>, B> doneCase,
      Function<Suspend<F, A>, B> suspendCase,
      Function<BindSuspend<F, S, A>, B> bindSuspendCase) {
    return suspendCase.apply(this);
  }
}

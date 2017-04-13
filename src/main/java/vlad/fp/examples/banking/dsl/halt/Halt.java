package vlad.fp.examples.banking.dsl.halt;

import vlad.fp.lib.Free;
import vlad.fp.lib.Unit;
import vlad.fp.lib.function.Function;
import vlad.fp.lib.higher.Functor;
import vlad.fp.lib.higher.Parametrized;

public final class Halt<F, T> implements Parametrized<Parametrized<Halt, F>, T> {
  public static <F, T> Halt<F, T> lift(Parametrized<Parametrized<Halt, F>, T> par) {
    return (Halt<F, T>) par;
  }

  public final Parametrized<F, Unit> p;

  public Halt(Parametrized<F, Unit> p) {
    this.p = p;
  }

  public static <F, T> Free<F, Unit> unhalt(Functor<F> functor, Free<Parametrized<Halt, F>, T> free) {
    return free.fold(functor(), arg -> Free.liftF(functor, Halt.lift(arg).p), arg -> Free.done(Unit.UNIT));
  }

  public static <F> Functor<Parametrized<Halt, F>> functor() {
    return new Functor<Parametrized<Halt, F>>() {
      @Override
      public <T, R> Halt<F, R> map(Parametrized<Parametrized<Halt, F>, T> fa, Function<T, R> f) {
        return new Halt<>(Halt.lift(fa).p);
      }
    };
  }
}

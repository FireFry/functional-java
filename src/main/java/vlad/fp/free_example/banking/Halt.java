package vlad.fp.free_example.banking;

import vlad.fp.lib.Free;
import vlad.fp.lib.Unit;
import vlad.fp.lib.function.Function;
import vlad.fp.lib.higher.Functor;
import vlad.fp.lib.higher.Parametrized;

final class Halt<F, T> implements Parametrized<Parametrized<Halt, F>, T> {
  static <F, T> Halt<F, T> lift(Parametrized<Parametrized<Halt, F>, T> par) {
    return (Halt<F, T>) par;
  }

  final Parametrized<F, Unit> p;

  Halt(Parametrized<F, Unit> p) {
    this.p = p;
  }

  static <F> Functor<Parametrized<Halt, F>> functor() {
    return new Functor<Parametrized<Halt, F>>() {
      @Override
      public <T, R> Halt<F, R> map(Parametrized<Parametrized<Halt, F>, T> fa, Function<T, R> f) {
        return new Halt<>(Halt.lift(fa).p);
      }
    };
  }

  static <F, T> Free<F, Unit> unhalt(Functor<F> functor, Free<Parametrized<Halt, F>, T> free) {
    return free.fold(functor(), arg -> Free.liftF(functor, Halt.lift(arg).p), arg -> Free.done(Unit.UNIT));
  }
}

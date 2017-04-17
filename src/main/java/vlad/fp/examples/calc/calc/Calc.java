package vlad.fp.examples.calc.calc;

import vlad.fp.lib.function.Function;
import vlad.fp.lib.function.Function2;
import vlad.fp.lib.higher.Functor;
import vlad.fp.lib.higher.Parametrized;

public abstract class Calc<T> implements Parametrized<Calc, T> {

  public static <T> Calc<T> lift(Parametrized<Calc, T> par) {
    return (Calc<T>) par;
  }

  public static <T> Add<T> add(T first, T second) {
    return new Add<>(first, second);
  }

  public static <T> Multiply<T> multiply(T first, T second) {
    return new Multiply<>(first, second);
  }

  public static <T> Number<T> number(int value) {
    return new Number<>(value);
  }

  Calc() {
    // sealed trait
  }

  public <R> R foldT(
      Function<Number<T>, R> numberCase,
      Function<Add<T>, R> addCase,
      Function<Multiply<T>, R> multiplyCase
  ) {
    Class<? extends Calc> cls = getClass();
    if (cls.equals(Number.class)) {
      return numberCase.apply((Number<T>) this);
    }
    if (cls.equals(Add.class)) {
      return addCase.apply((Add<T>) this);
    }
    if (cls.equals(Multiply.class)) {
      return multiplyCase.apply((Multiply<T>) this);
    }
    throw new AssertionError();
  }

  public <R> R fold(
      Function<Integer, R> numberCase,
      Function2<T, T, R> addCase,
      Function2<T, T, R> multiplyCase
  ) {
    return foldT(
        number -> numberCase.apply(number.value()),
        add -> addCase.apply(add.first(), add.second()),
        multiply -> multiplyCase.apply(multiply.first(), multiply.second())
    );
  }

  public <R> Calc<R> map(Function<T, R> f) {
    return foldT(
        number -> new Number<R>(number.value()),
        add -> new Add<>(f.apply(add.first()), f.apply(add.second())),
        multiply -> new Multiply<>(f.apply(multiply.first()), f.apply(multiply.second()))
    );
  }

  public static final Functor<Calc> FUNCTOR = new Functor<Calc>() {
    @Override
    public <T, R> Parametrized<Calc, R> map(Parametrized<Calc, T> fa, Function<T, R> f) {
      return lift(fa).map(f);
    }
  };

}

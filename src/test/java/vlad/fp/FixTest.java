package vlad.fp;

import static org.junit.Assert.assertEquals;
import static vlad.fp.Fix.fix;
import static vlad.fp.FixTest.Calc.FUNCTOR;
import static vlad.fp.FixTest.Calc.add;
import static vlad.fp.FixTest.Calc.lift;
import static vlad.fp.FixTest.Calc.multiply;
import static vlad.fp.FixTest.Calc.number;

import org.junit.Before;
import org.junit.Test;
import vlad.fp.higher.Functor;
import vlad.fp.higher.Parametrized;

import java.util.function.BiFunction;
import java.util.function.Function;

public class FixTest {

  interface Algebra<F, T> extends Function<Parametrized<F, T>, T> {}

  interface CoAlgebra<T, F> extends Function<T, Parametrized<F, T>> {}

  private static final Algebra<Calc, String> EXPLAIN = fa -> lift(fa).fold(
      String::valueOf,
      (x, y) -> "(" + x + " + " + y + ")",
      (x, y) -> "(" + x + " * " + y + ")"
  );

  private static final Algebra<Calc, Integer> EVALUATE = fa -> lift(fa).fold(
      x -> x,
      (x, y) -> x + y,
      (x, y) -> x * y
  );

  private static final CoAlgebra<Integer, Calc> EXPAND = x ->
      (x <= 2)
          ? number(x)
          : (x % 2 == 0)
              ? multiply(2, x / 2)
              : add(x - x / 2, x / 2);

  private Fix<Calc> calc;

  @Before
  public void setup() {
    calc = fix(multiply(
        fix(number(3)),
        fix(add(
            fix(number(1)),
            fix(number(2))))));
  }

  @Test
  public void testCatamorphismEvaluate() {
    assertEquals(9, (int) calc.cata(FUNCTOR, EVALUATE));
  }

  @Test
  public void testCatamorphismExplain() {
    assertEquals("(3 * (1 + 2))", calc.cata(FUNCTOR, EXPLAIN));
  }

  @Test
  public void testHylomorphism() {
    assertEquals("(((2 * 2) + (2 + 1)) + (2 * (2 + 1)))", Fix.hylo(FUNCTOR, 13, EXPAND, EXPLAIN));
  }

  public static final class Add<T> extends Calc<T> {
    private final T first;
    private final T second;

    Add(T first, T second) {
      this.first = first;
      this.second = second;
    }

    public T first() {
      return first;
    }

    public T second() {
      return second;
    }
  }

  public abstract static class Calc<T> implements vlad.fp.higher.Parametrized<Calc, T> {

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

    public <R> R match(
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
        BiFunction<T, T, R> addCase,
        BiFunction<T, T, R> multiplyCase
    ) {
      return match(
          number -> numberCase.apply(number.value()),
          add -> addCase.apply(add.first(), add.second()),
          multiply -> multiplyCase.apply(multiply.first(), multiply.second())
      );
    }

    public <R> Calc<R> map(Function<T, R> f) {
      return match(
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

  public static final class Multiply<T> extends Calc<T> {
    private final T first;
    private final T second;

    Multiply(T first, T second) {
      this.first = first;
      this.second = second;
    }

    public T first() {
      return first;
    }

    public T second() {
      return second;
    }
  }

  public static final class Number<T> extends Calc<T> {
    private final Integer value;

    Number(Integer value) {
      this.value = value;
    }

    public Integer value() {
      return value;
    }
  }
}

package vlad.fp.examples.calc;

import static vlad.fp.lib.Fix.fix;
import static vlad.fp.examples.calc.calc.Calc.add;
import static vlad.fp.examples.calc.calc.Calc.multiply;
import static vlad.fp.examples.calc.calc.Calc.number;

import vlad.fp.examples.calc.calc.Calc;
import vlad.fp.lib.function.Function;
import vlad.fp.lib.Fix;
import vlad.fp.lib.higher.Parametrized;

public class CalcExample {

  interface Algebra<F, T> extends Function<Parametrized<F, T>, T> {}

  interface CoAlgebra<T, F> extends Function<T, Parametrized<F, T>> {}

  public static void main(String[] args) {
    Fix<Calc> calc = fix(multiply(
        fix(number(3)),
        fix(add(
            fix(number(1)),
            fix(number(2))))));

    Algebra<Calc, Integer> eval = fa -> Calc.lift(fa).fold(
        x -> x,
        (x, y) -> x + y,
        (x, y) -> x * y
    );

    Algebra<Calc, String> explain = fa -> Calc.lift(fa).fold(
        String::valueOf,
        (x, y) -> "(" + x + " + " + y + ")",
        (x, y) -> "(" + x + " * " + y + ")"
    );

    System.out.print(calc.fold(Calc.FUNCTOR, explain)); // (3 * (1 + 2))
    System.out.print(" = ");
    System.out.println(calc.fold(Calc.FUNCTOR, eval)); // 9

    CoAlgebra<Integer, Calc> expand = x ->
        (x <= 2) 
            ? number(x) 
            : (x % 2 == 0) 
                ? multiply(2, x / 2) 
                : add(x - x / 2, x / 2);

    System.out.print("13 = ");
    System.out.println(Fix.hylo(Calc.FUNCTOR, 13, expand, explain)); // (((2 * 2) + (2 + 1)) + (2 * (2 + 1)))
  }

}

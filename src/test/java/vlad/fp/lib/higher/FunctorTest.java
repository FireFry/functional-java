package vlad.fp.lib.higher;

import static org.junit.Assert.assertEquals;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import vlad.fp.lib.Option;
import vlad.fp.lib.Seq;
import vlad.fp.lib.function.Function;

public class FunctorTest {

  @Test
  public void testCompose() {
    Functor<Parametrized<Seq, Option>> composite = Functor.compose(Seq.FUNCTOR, Option.FUNCTOR);
    Seq<Option<Integer>> result = Seq.lift(Parametrized.right(
        Seq.wrap(ImmutableList.of(Option.some(3), Option.none(), Option.some(6))), seq ->
        composite.map(seq, x -> x + 1) )).map(Option::lift);
    assertEquals(ImmutableList.of(Option.some(4), Option.none(), Option.some(7)), result.toList());
  }

}

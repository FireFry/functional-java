package vlad.fp.lib.higher;

import static org.junit.Assert.assertEquals;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import vlad.fp.lib.Option;
import vlad.fp.lib.ListWrap;

public class FunctorTest {

  @Test
  public void testCompose() {
    Functor<Parametrized<ListWrap, Option>> composite = Functor.compose(ListWrap.FUNCTOR, Option.FUNCTOR);
    ListWrap<Option<Integer>> result = ListWrap.lift(Parametrized.right(
        ListWrap.wrap(ImmutableList.of(Option.some(3), Option.none(), Option.some(6))), seq ->
        composite.map(seq, x -> x + 1) )).map(Option::lift);
    assertEquals(ImmutableList.of(Option.some(4), Option.none(), Option.some(7)), result.toList());
  }

}

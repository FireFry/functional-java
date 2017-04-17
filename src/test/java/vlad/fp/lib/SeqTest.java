package vlad.fp.lib;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class SeqTest {

  private static final Seq<Integer> list = listOfSize(1000000).run();

  @Test
  public void shouldCreateALargeList() {
    assertNotNull(list);
  }

  private static Trampoline<Seq<Integer>> listOfSize(int size) {
    return size < 1
        ? Trampoline.done(Seq.nil())
        : Trampoline.suspend(() -> listOfSize(size - 1)).map(tail -> Seq.cons(size, tail));
  }

  @Test
  public void shouldSumALargeList() {
    assertEquals(500000500000L, (long) list.foldLeft(0L, (a, b) -> a + b));
  }

  @Test
  public void shouldFilterList() {
    assertEquals(250000500000L, (long) list.filter(x -> x % 2 == 0).foldLeft(0L, (a, b) -> a + b));
  }

}

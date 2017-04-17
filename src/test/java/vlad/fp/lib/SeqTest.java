package vlad.fp.lib;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class SeqTest {

  @Test
  public void shouldCreateALargeList() {
    assertNotNull(listOfSize(1000000).run());
  }

  private static Trampoline<Seq<Integer>> listOfSize(int size) {
    return size < 1
        ? Trampoline.done(Seq.nil())
        : Trampoline.suspend(() -> listOfSize(size - 1)).map(tail -> Seq.cons(size, tail));
  }

  @Test
  public void shouldSumALargeList() {
    assertEquals(500000500000L, (long) listOfSize(1000000).run().foldLeft(0L, (a, b) -> a + b));
  }

}
package vlad.fp.lib;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class FPListTest {

  @Test
  public void shouldCreateALargeList() {
    FPList<Integer> list = listOfSize(1000000).run();
    assertNotNull(list);
  }

  private static Trampoline<FPList<Integer>> listOfSize(int size) {
    return size < 1
        ? Trampoline.done(FPList.nil())
        : Trampoline.suspend(() -> listOfSize(size - 1)).map(tail -> FPList.cons(size, tail));
  }

}

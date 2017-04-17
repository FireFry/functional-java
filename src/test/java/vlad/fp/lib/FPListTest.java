package vlad.fp.lib;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class FPListTest {

  @Test
  public void shouldCreateALargeList() {
    FPList<Integer> list = listOfSize(1000000);
    assertNotNull(list);
  }

  private static FPList<Integer> listOfSize(int size) {
    return listOfSizeTrampoline(size).run();
  }

  private static Trampoline<FPList<Integer>> listOfSizeTrampoline(int size) {
    return size < 1
        ? Trampoline.done(FPList.nil())
        : Trampoline.suspend(() -> listOfSizeTrampoline(size - 1)).map(tail -> FPList.cons(size, tail));
  }

}

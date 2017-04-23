package vlad.fp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TrampolineTest {

  @Test(expected = StackOverflowError.class)
  public void overflowEvenOdd() {
    assertTrue(even(1000000));
  }

  private boolean even(int n) {
    return n == 0 || odd(n - 1);
  }

  private boolean odd(int n) {
    return n != 0 && even(n - 1);
  }

  @Test
  public void testEvenOdd() {
    assertTrue(evenT(10000000).run());
  }

  private Trampoline<Boolean> evenT(int n) {
    return n == 0 ? Trampoline.done(true) : Trampoline.suspend(() -> oddT(n - 1));
  }

  private Trampoline<Boolean> oddT(int n) {
    return n == 0 ? Trampoline.done(false) : Trampoline.suspend(() -> evenT(n - 1));
  }

  @Test
  public void testSum() {
    assertEquals(500000500000L, (long) sum(1000000L).run());
  }

  private Trampoline<Long> sum(long n) {
    return n == 0 ? Trampoline.done(0L) : Trampoline.suspend(() -> sum(n - 1).map(x -> x + n));
  }
  
}

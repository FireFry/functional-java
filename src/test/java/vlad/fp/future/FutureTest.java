package vlad.fp.future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FutureTest {

  @Test
  public void testMultipleExecutors() {
    ExecutorService evenExecutor = Executors.newSingleThreadExecutor();
    ExecutorService oddExecutor = Executors.newSingleThreadExecutor();
    assertTrue(even(evenExecutor, oddExecutor, 100000).run());
  }

  private Future<Boolean> even(ExecutorService evenExecutor, ExecutorService oddExecutor, int n) {
    return Future.fork(
        () -> n == 0
            ? Future.now(true)
            : odd(evenExecutor, oddExecutor, n - 1),
        evenExecutor);
  }

  private Future<Boolean> odd(ExecutorService evenExecutor, ExecutorService oddExecutor, int n) {
    return Future.fork(
        () -> n == 0
            ? Future.now(false)
            : even(evenExecutor, oddExecutor, n - 1),
        oddExecutor);
  }

  @Test
  public void testSum() {
    assertEquals(5000050000L, (long) sum(100000L, Executors.newSingleThreadExecutor()).run());
  }

  private Future<Long> sum(long n, ExecutorService pool) {
    return Future.fork(() -> n == 0
        ? Future.now(0L)
        : Future.suspend(() -> sum(n - 1, pool).map(x -> x + n)), pool);
  }

}

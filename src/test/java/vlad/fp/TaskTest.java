package vlad.fp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskTest {

  @Test
  public void testMultipleExecutors() {
    ExecutorService evenExecutor = Executors.newSingleThreadExecutor();
    ExecutorService oddExecutor = Executors.newSingleThreadExecutor();
    assertTrue(even(evenExecutor, oddExecutor, 100000).run());
  }

  private Task<Boolean> even(ExecutorService evenExecutor, ExecutorService oddExecutor, int n) {
    return Task.fork(
        () -> n == 0
            ? Task.now(true)
            : odd(evenExecutor, oddExecutor, n - 1),
        evenExecutor);
  }

  private Task<Boolean> odd(ExecutorService evenExecutor, ExecutorService oddExecutor, int n) {
    return Task.fork(
        () -> n == 0
            ? Task.now(false)
            : even(evenExecutor, oddExecutor, n - 1),
        oddExecutor);
  }

  @Test
  public void testSum() {
    assertEquals(5000050000L, (long) sum(100000L, Executors.newSingleThreadExecutor()).run());
  }

  private Task<Long> sum(long n, ExecutorService pool) {
    return Task.fork(() -> n == 0
        ? Task.now(0L)
        : Task.suspend(() -> sum(n - 1, pool).map(x -> x + n)), pool);
  }

}

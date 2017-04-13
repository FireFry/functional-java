package vlad.fp.lib;

import static vlad.fp.lib.Utils.voidF;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

public abstract class Transformers {
  private Transformers() {
    // non-instantiatable class
  }

  public static <T> Future<List<T>> liftFuture(List<Future<T>> list) {
    return list.isEmpty() ? Future.now(ImmutableList.of()) : Future.async(voidF(cb -> {
      AtomicReferenceArray<T> results = new AtomicReferenceArray<>(list.size());
      AtomicInteger remaining = new AtomicInteger(list.size());
      for (int i = 0; i < list.size(); i++) {
        final int index = i;
        Future<T> future = list.get(i);
        future.runAsync(x -> {
          results.set(index, x);
          if (remaining.decrementAndGet() == 0) {
            Builder<T> builder = ImmutableList.builder();
            for (int j = 0; j < results.length(); j++) {
              builder.add(results.get(j));
            }
            return cb.apply(builder.build());
          } else {
            return null;
          }
        });
      }
    }));
  }

  public static <T> Task<List<T>> liftTask(List<Task<T>> list) {
    return list.isEmpty() ? Task.now(ImmutableList.of()) : Task.async(voidF(cb -> {
      AtomicReferenceArray<T> results = new AtomicReferenceArray<>(list.size());
      AtomicBoolean failed = new AtomicBoolean();
      AtomicInteger remaining = new AtomicInteger(list.size());
      for (int i = 0; i < list.size(); i++) {
        final int index = i;
        Task<T> task = list.get(i);
        task.runAsync(e -> e.fold(
            th -> {
              if (failed.compareAndSet(false, true)) {
                return cb.apply(Either.left(th));
              } else {
                return null;
              }
            },
            x -> {
              results.set(index, x);
              if (remaining.decrementAndGet() == 0) {
                Builder<T> builder = ImmutableList.builder();
                for (int j = 0; j < results.length(); j++) {
                  builder.add(results.get(j));
                }
                return cb.apply(Either.right(builder.build()));
              } else {
                return null;
              }
            })
        );
      }
    }));
  }
}

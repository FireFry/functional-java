package vlad.fp.lib;

import static com.google.common.collect.ImmutableList.toImmutableList;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;

import java.util.Comparator;
import java.util.List;
import vlad.fp.lib.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class Seq<T> {
  @SuppressWarnings("unchecked")
  private static final Seq NIL = new Seq(ImmutableList.of());

  private final ImmutableList<T> list;

  @SuppressWarnings("unchecked")
  public static <T> Seq<T> nil() {
    return NIL;
  }

  public static <T> Seq<T> of(T value) {
    return new Seq<>(ImmutableList.of(value));
  }

  public static <T> Seq<T> wrap(List<T> list) {
    return new Seq<>(ImmutableList.copyOf(list));
  }

  private Seq(ImmutableList<T> list) {
    this.list = list;
  }

  private <R> Seq<R> transform(Function<Stream<T>, Stream<R>> f) {
    return Seq.wrap(f.apply(list.stream()).collect(toImmutableList()));
  }

  public <R> Seq<R> flatMap(Function<T, Seq<R>> f) {
    return transform(s -> s.flatMap(e -> f.apply(e).list.stream()));
  }

  public <R> Seq<R> map(Function<T, R> f) {
    return transform(s -> s.map(f));
  }

  public Seq<T> sorted(Comparator<T> comparator) {
    return wrap(Ordering.from(comparator).sortedCopy(list));
  }

  public Seq<T> filter(Predicate<T> p) {
    return transform(s -> s.filter(p));
  }

  public <R> R apply(Function<Seq<T>, R> f) {
    return f.apply(this);
  }

  public List<T> toList() {
    return list;
  }

  public Seq<T> take(int n) {
    return transform(s -> s.limit(n));
  }

  public int size() {
    return list.size();
  }
}

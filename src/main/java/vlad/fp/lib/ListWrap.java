package vlad.fp.lib;

import static com.google.common.collect.ImmutableList.toImmutableList;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;

import java.util.Comparator;
import java.util.List;
import vlad.fp.lib.function.Function;
import vlad.fp.lib.higher.Functor;
import vlad.fp.lib.higher.Parametrized;

import java.util.function.Predicate;
import java.util.stream.Stream;

public final class ListWrap<T> implements Parametrized<ListWrap, T> {
  public static <T> ListWrap<T> lift(Parametrized<ListWrap, T> par) {
    return (ListWrap<T>) par;
  }

  @SuppressWarnings("unchecked")
  private static final ListWrap NIL = new ListWrap(ImmutableList.of());

  private final ImmutableList<T> list;

  @SuppressWarnings("unchecked")
  public static <T> ListWrap<T> nil() {
    return NIL;
  }

  public static <T> ListWrap<T> of(T value) {
    return new ListWrap<>(ImmutableList.of(value));
  }

  public static <T> ListWrap<T> wrap(List<T> list) {
    return new ListWrap<>(ImmutableList.copyOf(list));
  }

  private ListWrap(ImmutableList<T> list) {
    this.list = list;
  }

  private <R> ListWrap<R> transform(Function<Stream<T>, Stream<R>> f) {
    return ListWrap.wrap(f.apply(list.stream()).collect(toImmutableList()));
  }

  public <R> ListWrap<R> flatMap(Function<T, ListWrap<R>> f) {
    return transform(s -> s.flatMap(e -> f.apply(e).list.stream()));
  }

  public <R> ListWrap<R> map(Function<T, R> f) {
    return transform(s -> s.map(f));
  }

  public ListWrap<T> sorted(Comparator<T> comparator) {
    return wrap(Ordering.from(comparator).sortedCopy(list));
  }

  public ListWrap<T> filter(Predicate<T> p) {
    return transform(s -> s.filter(p));
  }

  public <R> R apply(Function<ListWrap<T>, R> f) {
    return f.apply(this);
  }

  public List<T> toList() {
    return list;
  }

  public ListWrap<T> take(int n) {
    return transform(s -> s.limit(n));
  }

  public int size() {
    return list.size();
  }

  public static final Functor<ListWrap> FUNCTOR = new Functor<ListWrap>() {
    @Override
    public <T, R> Parametrized<ListWrap, R> map(Parametrized<ListWrap, T> fa, Function<T, R> f) {
      return lift(fa).map(f);
    }
  };
}

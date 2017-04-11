package vlad.fp.lib.tuple;

public final class Tuple2<T1, T2> {
  private final T1 first;
  private final T2 second;

  public static <T1, T2> Tuple2<T1, T2> of(T1 first, T2 second) {
    return new Tuple2<>(first, second);
  }

  private Tuple2(T1 first, T2 second) {
    this.first = first;
    this.second = second;
  }

  public T1 first() {
    return first;
  }

  public T2 second() {
    return second;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Tuple2<?, ?> tuple2 = (Tuple2<?, ?>) o;

    if (first != null ? !first.equals(tuple2.first) : tuple2.first != null) {
      return false;
    }
    return second != null ? second.equals(tuple2.second) : tuple2.second == null;
  }

  @Override
  public int hashCode() {
    int result = first != null ? first.hashCode() : 0;
    result = 31 * result + (second != null ? second.hashCode() : 0);
    return result;
  }
}

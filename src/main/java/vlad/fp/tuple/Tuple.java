package vlad.fp.tuple;

import java.util.Objects;

public final class Tuple<F, S> {
    private final F first;
    private final S second;

    public static <F, S> Tuple<F, S> of(F first, S second) {
        return new Tuple<>(first, second);
    }

    public Tuple(F first, S second) {
        this.first = Objects.requireNonNull(first);
        this.second = Objects.requireNonNull(second);
    }

    public F first() {
        return first;
    }

    public S second() {
        return second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tuple<?, ?> tuple = (Tuple<?, ?>) o;

        if (!first.equals(tuple.first)) return false;
        return second.equals(tuple.second);
    }

    @Override
    public int hashCode() {
        int result = first.hashCode();
        result = 31 * result + second.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }
}

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
}

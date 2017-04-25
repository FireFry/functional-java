package vlad.fp.utils;

import java.util.Objects;

public abstract class TypeAlias<A> {
    private final A get;

    public TypeAlias(A get) {
        this.get = Objects.requireNonNull(get);
    }

    public final A get() {
        return get;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TypeAlias<?> typeAlias = (TypeAlias<?>) o;

        return get.equals(typeAlias.get);
    }

    @Override
    public final int hashCode() {
        return get.hashCode();
    }

    @Override
    public final String toString() {
        return get.toString();
    }
}

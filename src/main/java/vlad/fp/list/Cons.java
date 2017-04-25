package vlad.fp.list;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public final class Cons<A> extends List<A> {
    private final A head;
    private final List<A> tail;

    public Cons(A head, List<A> tail) {
        this.head = Objects.requireNonNull(head);
        this.tail = Objects.requireNonNull(tail);
    }

    public A head() {
        return head;
    }

    public List<A> tail() {
        return tail;
    }

    @Override
    public <B> List<B> map(Function<A, B> function) {
        return List.cons(function.apply(head), tail.map(function));
    }

    @Override
    public <B> B match(Function<Nil<A>, B> nilCase, Function<Cons<A>, B> consCase) {
        return consCase.apply(this);
    }

    @Override
    public <B> B matchVal(Supplier<B> nilCase, BiFunction<A, List<A>, B> consCase) {
        return consCase.apply(head, tail);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Cons<?> cons = (Cons<?>) o;

        if (!head.equals(cons.head)) return false;
        return tail.equals(cons.tail);
    }

    @Override
    public int hashCode() {
        int result = head.hashCode();
        result = 31 * result + tail.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return head + " : " + tail;
    }
}

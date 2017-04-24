package vlad.fp.list;

import vlad.fp.Trampoline;
import vlad.fp.utils.NestedFunction;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class List<A> {

    public static <A> List<A> cons(A head, List<A> tail) {
        return new Cons<>(head, tail);
    }

    public static <A> List<A> nil() {
        return Nil.get();
    }

    public static <A> List<A> of(A... values) {
        return new NestedFunction() {
            <A> Trampoline<List<A>> ofTrampoline(A[] values, int from) {
                return from < values.length
                        ? Trampoline.suspend(() -> ofTrampoline(values, from + 1).map(tail -> List.cons(values[from], tail)))
                        : Trampoline.done(List.nil());
            }
        }.ofTrampoline(values, 0).run();
    }

    List() {

    }

    public abstract <B> B match(Function<Nil<A>, B> nilCase, Function<Cons<A>, B> consCase);

    public abstract <B> B matchVal(Supplier<B> nilCase, BiFunction<A, List<A>, B> consCase);

}

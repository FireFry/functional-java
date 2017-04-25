package vlad.fp.list;

import vlad.fp.Trampoline;
import vlad.fp.maybe.Maybe;
import vlad.fp.tailrec.TailRec;
import vlad.fp.utils.Matcher;
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
        return copyOf(values);
    }

    public static <A> List<A> copyOf(A[] values) {
        return new NestedFunction() {
            <A> Trampoline<List<A>> copyOf(A[] values, int from) {
                return from < values.length
                        ? Trampoline.suspend(() -> copyOf(values, from + 1).map(tail -> List.cons(values[from], tail)))
                        : Trampoline.done(List.nil());
            }
        }.copyOf(values, 0).run();
    }

    List() {

    }

    public abstract <B> B match(Function<Nil<A>, B> nilCase, Function<Cons<A>, B> consCase);

    public abstract <B> B matchVal(Supplier<B> nilCase, BiFunction<A, List<A>, B> consCase);

    public <B> Maybe<B> match(ListMatcher<A, B> matcher) {
        return matcher.match(this);
    }

    public <B> B matchRec(ListMatcher<A, B> matcher) {
        return new NestedFunction() {
            TailRec<B> matchRec(List<A> list, ListMatcher<A, B> matcher) {
                return list.match(matcher).matchVal(
                        () -> list.match(
                                nil -> Matcher.unmatched(),
                                cons -> TailRec.suspend(() -> matchRec(cons.tail(), matcher))
                        ),
                        TailRec::done
                );
            }
        }.matchRec(this, matcher).eval();
    }

}

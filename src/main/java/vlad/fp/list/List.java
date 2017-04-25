package vlad.fp.list;

import vlad.fp.Trampoline;
import vlad.fp.maybe.Maybe;
import vlad.fp.tailrec.TailRec;
import vlad.fp.utils.Matcher;
import vlad.fp.utils.NestedFunction;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static vlad.fp.list.ListMatcher.whenTail;

public abstract class List<A> {

    public static <A> List<A> cons(A head) {
        return new Cons<>(head, nil());
    }

    public static <A> List<A> cons(A head, List<A> tail) {
        return new Cons<>(head, tail);
    }

    public static <A> List<A> nil() {
        return Nil.get();
    }

    @SafeVarargs
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

    public abstract <B> List<B> map(Function<A, B> function);

    public abstract <B> B match(Function<Nil<A>, B> nilCase, Function<Cons<A>, B> consCase);

    public abstract <B> B matchVal(Supplier<B> nilCase, BiFunction<A, List<A>, B> consCase);

    @SafeVarargs
    public final <B> B match(ListMatcher<A, B>... matchers) {
        return new NestedFunction() {
            B match(List<A> list, List<ListMatcher<A, B>> matchers) {
                return matchers.matchVal(
                        Matcher::unmatched,
                        (head, tail) -> head.match(list).matchVal(
                                () -> match(list, tail),
                                Function.identity()
                        )
                );
            }
        }.match(this, List.copyOf(matchers));
    }

    public <B> B matchRec(ListMatcher<A, B> matcher) {
        return new NestedFunction() {
            B matchRec(List<A> list, ListMatcher<A, B> matcher) {
                return list.match(
                        matcher,
                        whenTail(tail -> matchRec(tail, matcher))
                );
            }
        }.matchRec(this, matcher);
    }

}

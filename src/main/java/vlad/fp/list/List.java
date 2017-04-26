package vlad.fp.list;

import static vlad.fp.list.ListMatcher.whenTail;

import com.google.common.primitives.Chars;
import vlad.fp.Trampoline;
import vlad.fp.utils.Matcher;
import vlad.fp.utils.NestedFunction;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class List<A> {

    public static List<Character> ofChars(String s) {
      return copyOf(Chars.asList(s.toCharArray()));
    }

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

    public static <A> List<A> copyOf(Iterable<A> iterable) {
        List<A> buffer = List.nil();
        for (A value : iterable) {
            buffer = List.cons(value, buffer);
        }
        return reverse(buffer);
    }

    private static <A> List<A> reverse(List<A> list) {
        return new NestedFunction() {
            Trampoline<List<A>> reverse(List<A> list, List<A> buffer) {
                return list.matchVal(
                    () -> Trampoline.done(buffer),
                    (head, tail) -> Trampoline.suspend(() -> reverse(tail, cons(head, buffer)))
                );
            }
        }.reverse(list, nil()).run();
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

    public List<A> append(List<A> other) {
        return new NestedFunction() {
            Trampoline<List<A>> append(List<A> list, List<A> other) {
                return list.matchVal(
                        () -> Trampoline.done(other),
                        (x, xs) -> Trampoline.suspend(() -> append(xs, other).map(tail -> Cons.cons(x, tail)))
                );
            }
        }.append(this, other).run();
    }
}

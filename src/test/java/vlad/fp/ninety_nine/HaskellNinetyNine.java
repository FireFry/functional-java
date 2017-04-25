package vlad.fp.ninety_nine;

import com.google.common.primitives.Chars;
import org.junit.Test;
import vlad.fp.Trampoline;
import vlad.fp.either.Either;
import vlad.fp.list.List;
import vlad.fp.tailrec.TailRec;
import vlad.fp.utils.Matcher;
import vlad.fp.utils.NestedFunction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static vlad.fp.list.ListMatcher.any;
import static vlad.fp.list.ListMatcher.nil;

public class HaskellNinetyNine {

    private static List<Character> listOfChars(String s) {
        return List.copyOf(Chars.asList(s.toCharArray()).toArray(new Character[s.length()]));
    }

    @Test
    public void problem1() {
        assertEquals(4, (int) last(List.of(1, 2, 3, 4)));
        assertEquals('z', (char) last(List.of('x', 'y', 'z')));
    }

    private static <A> A last(List<A> list) {
        return list.matchRec(any(x -> nil(() -> x)));
    }

    @Test
    public void problem2() {
        assertEquals(3, (int) butLast(List.of(1, 2, 3, 4)));
        assertEquals('y', (char) butLast(List.of('x', 'y', 'z')));
    }

    private static <A> A butLast(List<A> list) {
        return list.matchRec(any(x -> any(() -> nil(() -> x))));
    }

    @Test
    public void problem3() {
        assertEquals(2, (int) elementAt(List.of(1, 2, 3), 2));
        assertEquals('e', (char) elementAt(listOfChars("haskell"), 5));
    }

    private static <A> A elementAt(List<A> list, int k) {
        return new NestedFunction() {
            TailRec<A> elementAt(List<A> list, int k) {
                return k < 1 ?
                        Matcher.unmatched() :
                        list.matchVal(
                                Matcher::unmatched,
                                (head, tail) -> k == 1
                                        ? TailRec.done(head)
                                        : TailRec.suspend(() -> elementAt(tail, k - 1))
                        );
            }
        }.elementAt(list, k).eval();
    }

    @Test
    public void problem4() {
        assertEquals(3, length(List.of(123, 456, 789)));
        assertEquals(13, length(listOfChars("Hello, world!")));
    }

    private static <A> int length(List<A> list) {
        return new NestedFunction() {
            Trampoline<Integer> length(List<A> list) {
                return list.match(
                        nil -> Trampoline.done(0),
                        cons -> Trampoline.suspend(() -> length(cons.tail())).map(tailLength -> tailLength + 1)
                );
            }
        }.length(list).run();
    }

    @Test
    public void problem5() {
        assertEquals(listOfChars("!amanap ,lanac a ,nalp a ,nam A"), reverse(listOfChars("A man, a plan, a canal, panama!")));
        assertEquals(List.of(4, 3, 2, 1), reverse(List.of(1, 2, 3, 4)));
    }

    private static <A> List<A> reverse(List<A> list) {
        return new NestedFunction() {
            TailRec<List<A>> reverse(List<A> list, List<A> buffer) {
                return list.matchVal(
                        () -> TailRec.done(buffer),
                        (head, tail) -> TailRec.suspend(() -> reverse(tail, List.cons(head, buffer)))
                );
            }
        }.reverse(list, List.nil()).eval();
    }

    @Test
    public void problem6() {
        assertFalse(isPalindrome(List.of(1, 2, 3)));
        assertTrue(isPalindrome(listOfChars("madamimadam")));
        assertTrue(isPalindrome(List.of(1, 2, 4, 8, 16, 8, 4, 2, 1)));
    }

    private static <A> boolean isPalindrome(List<A> list) {
        return list.equals(reverse(list));
    }

    @Test
    public void problem7() {
        assertEquals(List.of(5), flatten(nelem(5)));
        assertEquals(List.of(1, 2, 3, 4, 5), flatten(nlist(nelem(1), nlist(nelem(2), nlist(nelem(3), nelem(4)), nelem(5)))));
        assertEquals(List.nil(), flatten(nlist()));
    }

    private static <A> List<A> flatten(NestedList<A> nestedList) {
        return nestedList.either.matchVal(
                List::of,
                list -> new NestedFunction() {
                    List<A> flatten(List<NestedList<A>> list, List<A> buffer) {
                        return list.matchVal(
                                () -> buffer,
                                (head, tail) -> head.either.matchVal(
                                        a -> List.cons(a, flatten(tail, buffer)),
                                        nested -> flatten(nested, flatten(tail, buffer))
                                )
                        );
                    }
                }.flatten(list, List.nil())
        );
    }

    static final class NestedList<A> {
        private final Either<A, List<NestedList<A>>> either;

        private NestedList(Either<A, List<NestedList<A>>> either) {
            this.either = either;
        }
    }

    static <A> NestedList<A> nelem(A a) {
        return new NestedList<>(Either.left(a));
    }

    @SafeVarargs
    static <A> NestedList<A> nlist(NestedList<A>... list) {
        return new NestedList<>(Either.right(List.copyOf(list)));
    }

}

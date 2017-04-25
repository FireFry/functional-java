package vlad.fp.ninety_nine;

import com.google.common.primitives.Chars;
import org.junit.Test;
import vlad.fp.Trampoline;
import vlad.fp.list.List;
import vlad.fp.tailrec.TailRec;
import vlad.fp.utils.Matcher;
import vlad.fp.utils.NestedFunction;

import static org.junit.Assert.assertEquals;
import static vlad.fp.list.ListMatcher.any;
import static vlad.fp.list.ListMatcher.nil;

public class HaskellNinetyNine {

    private static List<Character> listOfChars(String s) {
        return List.of(Chars.asList(s.toCharArray()).toArray(new Character[s.length()]));
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

}

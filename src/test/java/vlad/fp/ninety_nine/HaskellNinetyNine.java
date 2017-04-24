package vlad.fp.ninety_nine;

import org.junit.Test;
import vlad.fp.list.List;
import vlad.fp.tailrec.TailRec;
import vlad.fp.utils.Matcher;
import vlad.fp.utils.NestedFunction;

import static org.junit.Assert.*;

public class HaskellNinetyNine {

    @Test
    public void problem1() {
        assertEquals(4, (int) last(List.of(1, 2, 3, 4)));
        assertEquals('z', (char) last(List.of('x', 'y', 'z')));
    }

    private static <A> A last(List<A> list) {
        return new NestedFunction() {

            <A> TailRec<A> lastTailRec(List<A> list) {
                return list.matchVal(
                        Matcher::unmatched,
                        (head, tail) -> tail.match(
                                nil -> TailRec.done(head),
                                cons -> TailRec.suspend(() -> lastTailRec(cons))));
            }

        }.lastTailRec(list).eval();
    }

    @Test
    public void problem2() {
        assertEquals(3, (int) butLast(List.of(1, 2, 3, 4)));
        assertEquals('y', (char) butLast(List.of('x', 'y', 'z')));
    }

    private static <A> A butLast(List<A> list) {
        return new NestedFunction() {

            <A> TailRec<A> butLastTailRec(List<A> list) {
                return list.matchVal(
                        Matcher::unmatched,
                        (x, tail) -> tail.matchVal(
                                Matcher::unmatched,
                                (y, tail2) -> tail2.match(
                                        nil -> TailRec.done(x),
                                        cons -> TailRec.suspend(() -> butLastTailRec(tail)))));
            }

        }.butLastTailRec(list).eval();
    }

}

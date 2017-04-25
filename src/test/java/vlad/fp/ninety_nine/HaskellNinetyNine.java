package vlad.fp.ninety_nine;

import com.google.common.primitives.Chars;
import org.junit.Test;
import vlad.fp.Trampoline;
import vlad.fp.either.Either;
import vlad.fp.list.List;
import vlad.fp.tailrec.TailRec;
import vlad.fp.tuple.Tuple;
import vlad.fp.utils.Matcher;
import vlad.fp.utils.NestedFunction;

import static org.junit.Assert.*;
import static vlad.fp.list.ListMatcher.*;

public class HaskellNinetyNine {

    private static List<Character> listOfChars(String s) {
        return List.copyOf(Chars.asList(s.toCharArray()).toArray(new Character[s.length()]));
    }

    /**
     * Problem 1
     * =========
     *
     * Find the last element of a list.
     *
     * Example in Haskell:
     *
     * Prelude> myLast [1,2,3,4]
     * 4
     *
     * Prelude> myLast ['x','y','z']
     * 'z'
     */
    @Test
    public void problem1() {
        assertEquals(4, (int) last(List.of(1, 2, 3, 4)));
        assertEquals('z', (char) last(List.of('x', 'y', 'z')));
    }

    private static <A> A last(List<A> list) {
        return list.matchRec(whenCons(x -> whenNil(() -> x)));
    }

    /**
     * Problem 2
     * =========
     *
     * Find the last but one element of a list.
     *
     * Example in Haskell:
     *
     * Prelude> myButLast [1,2,3,4]
     * 3
     *
     * Prelude> myButLast ['a'..'z']
     * 'y'
     */
    @Test
    public void problem2() {
        assertEquals(3, (int) butLast(List.of(1, 2, 3, 4)));
        assertEquals('y', (char) butLast(List.of('x', 'y', 'z')));
    }

    private static <A> A butLast(List<A> list) {
        return list.matchRec(whenCons(x -> whenCons(() -> whenNil(() -> x))));
    }

    /**
     * Problem 3
     * =========
     *
     * Find the K'th element of a list. The first element in the list is number 1.
     *
     * Example in Haskell:
     *
     * Prelude> elementAt [1,2,3] 2
     * 2
     *
     * Prelude> elementAt "haskell" 5
     * 'e'
     */
    @Test
    public void problem3() {
        assertEquals(2, (int) elementAt(List.of(1, 2, 3), 2));
        assertEquals('e', (char) elementAt(listOfChars("haskell"), 5));
    }

    private static <A> A elementAt(List<A> list, int k) {
        return k < 1 ? Matcher.unmatched() : new NestedFunction() {
            TailRec<A> elementAt(List<A> list, int k) {
                return list.matchVal(
                        Matcher::unmatched,
                        (head, tail) -> k == 1
                                ? TailRec.done(head)
                                : TailRec.suspend(() -> elementAt(tail, k - 1))
                );
            }
        }.elementAt(list, k).eval();
    }

    /**
     * Problem 4
     * =========
     *
     * Find the number of elements of a list.
     *
     * Example in Haskell:
     *
     * Prelude> myLength [123, 456, 789]
     * 3
     *
     * Prelude> myLength "Hello, world!"
     * 13
     */
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

    /**
     * Problem 5
     * =========
     *
     * Reverse a list.
     *
     * Example in Haskell:
     *
     * Prelude> myReverse "A man, a plan, a canal, panama!"
     * "!amanap ,lanac a ,nalp a ,nam A"
     *
     * Prelude> myReverse [1,2,3,4]
     * [4,3,2,1]
     */
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

    /**
     * Problem 6
     * =========
     *
     * Find out whether a list is a palindrome. A palindrome can be read forward or backward; e.g. (x a m a x).
     *
     * Example in Haskell:
     *
     * Prelude> isPalindrome [1,2,3]
     * False
     *
     * Prelude> isPalindrome "madamimadam"
     * True
     *
     * Prelude> isPalindrome [1,2,4,8,16,8,4,2,1]
     * True
     */
    @Test
    public void problem6() {
        assertFalse(isPalindrome(List.of(1, 2, 3)));
        assertTrue(isPalindrome(listOfChars("madamimadam")));
        assertTrue(isPalindrome(List.of(1, 2, 4, 8, 16, 8, 4, 2, 1)));
    }

    private static <A> boolean isPalindrome(List<A> list) {
        return list.equals(reverse(list));
    }

    /**
     * Problem 7
     * =========
     *
     * Flatten a nested list structure.
     *
     * Transform a list, possibly holding lists as elements into a `flat' list by replacing each list with its elements (recursively).
     *
     * Example in Haskell:
     *
     * We have to define a new data type, because lists in Haskell are homogeneous.
     *
     * data NestedList a = Elem a | List [NestedList a]
     *
     * Prelude> flatten (Elem 5)
     * [5]
     *
     * Prelude> flatten (List [Elem 1, List [Elem 2, List [Elem 3, Elem 4], Elem 5]])
     * [1,2,3,4,5]
     *
     * Prelude> flatten (List [])
     * []
     */
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

    /**
     * Problem 8
     * =========
     *
     * Eliminate consecutive duplicates of list elements.
     *
     * If a list contains repeated elements they should be replaced with a single copy of the element. The order of the elements should not be changed.
     *
     * Example in Haskell:
     *
     * Prelude> compress "aaaabccaadeeee"
     * "abcade"
     */
    @Test
    public void problem8() {
        assertEquals(listOfChars("abcade"), compress(listOfChars("aaaabccaadeeee")));
    }

    private static <A> List<A> compress(List<A> list) {
        return list.match(
                whenCons((x, tailX) -> whenCons(y -> done(() -> x.equals(y) ? compress(tailX) : List.cons(x, compress(tailX))))),
                whenOther(() -> list)
        );
    }

    /**
     * Problem 9
     * =========
     *
     * Pack consecutive duplicates of list elements into sublists. If a list contains repeated elements they should be placed in separate sublists.
     * 
     * Example in Haskell:
     * 
     * Prelude> pack ['a', 'a', 'a', 'a', 'b', 'c', 'c', 'a', 'a', 'd', 'e', 'e', 'e', 'e']
     * ["aaaa","b","cc","aa","d","eeee"]
     */
    @Test
    public void problem9() {
        assertEquals(List.of("aaaa", "b", "cc", "aa", "d", "eeee").map(s -> listOfChars(s)), pack(listOfChars("aaaabccaadeeee")));
    }

    private static <A> List<List<A>> pack(List<A> list) {
        return new NestedFunction() {
            List<List<A>> pack(List<A> list) {
                return list.matchVal(
                        List::nil,
                        (x, xs) -> pack(x, List.cons(x), xs)
                );
            }

            List<List<A>> pack(A current, List<A> buffer, List<A> list) {
                return list.matchVal(
                        () -> List.of(buffer),
                        (x, xs) -> x.equals(current) ?
                                pack(x, List.cons(x, buffer), xs) :
                                List.cons(buffer, pack(list))
                );
            }
        }.pack(list);
    }

    /**
     * Prelude 10
     * ==========
     *
     * Run-length encoding of a list. Use the result of problem P09 to implement the so-called run-length encoding data
     * compression method. Consecutive duplicates of elements are encoded as lists (N E) where N is the number of
     * duplicates of the element E.
     *
     * Example in Haskell:
     *
     * Prelude> encode "aaaabccaadeeee"
     * [(4,'a'),(1,'b'),(2,'c'),(2,'a'),(1,'d'),(4,'e')]
     */
    @Test
    public void problem10() {
        assertEquals(List.of(
                Tuple.of(4, 'a'),
                Tuple.of(1, 'b'),
                Tuple.of(2, 'c'),
                Tuple.of(2, 'a'),
                Tuple.of(1, 'd'),
                Tuple.of(4, 'e')), encode(listOfChars("aaaabccaadeeee")));
    }

    private <A> List<Tuple<Integer, A>> encode(List<A> list) {
        return new NestedFunction() {
            List<Tuple<Integer, A>> encode(List<A> list) {
                return list.matchVal(
                        List::nil,
                        (x, xs) -> encode(x, 1, xs)
                );
            }

            List<Tuple<Integer, A>> encode(A current, int count, List<A> list) {
                return list.matchVal(
                        () -> List.of(Tuple.of(count, current)),
                        (x, xs) -> x.equals(current) ?
                                encode(x, count + 1, xs) :
                                List.cons(Tuple.of(count, current), encode(list))
                );
            }
        }.encode(list);
    }

}

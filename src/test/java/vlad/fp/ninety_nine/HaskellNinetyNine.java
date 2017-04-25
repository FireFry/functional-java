package vlad.fp.ninety_nine;

import com.google.common.primitives.Chars;
import org.junit.Test;
import vlad.fp.Trampoline;
import vlad.fp.either.Either;
import vlad.fp.list.List;
import vlad.fp.tuple.Tuple;
import vlad.fp.utils.Matcher;
import vlad.fp.utils.NestedFunction;
import vlad.fp.utils.TypeAlias;

import static org.junit.Assert.*;
import static vlad.fp.list.ListMatcher.*;

/**
 * H-99: Ninety-Nine Haskell Problems
 * ==================================
 *
 * Java translations of Ninety-Nine Haskell Problems.
 *
 * See: https://wiki.haskell.org/99_questions
 */
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
            Trampoline<A> elementAt(List<A> list, int k) {
                return list.matchVal(
                        Matcher::unmatched,
                        (head, tail) -> k == 1
                                ? Trampoline.done(head)
                                : Trampoline.suspend(() -> elementAt(tail, k - 1))
                );
            }
        }.elementAt(list, k).run();
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
            Trampoline<List<A>> reverse(List<A> list, List<A> buffer) {
                return list.matchVal(
                        () -> Trampoline.done(buffer),
                        (head, tail) -> Trampoline.suspend(() -> reverse(tail, List.cons(head, buffer)))
                );
            }
        }.reverse(list, List.nil()).run();
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
        return new NestedFunction() {
            List<A> flatten(NestedList<A> nestedList) {
                return nestedList.either.matchVal(
                        List::of,
                        list -> flatten(list, List.nil())
                );
            }

            List<A> flatten(List<NestedList<A>> list, List<A> buffer) {
                return list.matchVal(
                        () -> buffer,
                        (head, tail) -> head.either.matchVal(
                                a -> List.cons(a, flatten(tail, buffer)),
                                nested -> flatten(nested, flatten(tail, buffer))
                        )
                );
            }
        }.flatten(nestedList);
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
                Tuple.of(4, 'e')
        ), encode(listOfChars("aaaabccaadeeee")));
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

    /**
     * Problem 11
     * ==========
     *
     * Modified run-length encoding.
     * 
     * Modify the result of problem 10 in such a way that if an element has no duplicates it is simply copied into the 
     * result list. Only elements with duplicates are transferred as (N E) lists.
     * 
     * Example in Haskell:
     * 
     * P11> encodeModified "aaaabccaadeeee"
     * [Multiple 4 'a',Single 'b',Multiple 2 'c', Multiple 2 'a',Single 'd',Multiple 4 'e']
     */
    @Test
    public void problem11() {
        assertEquals(List.of(mult(4, 'a'), single('b'), mult(2, 'c'), mult(2, 'a'), single('d'), mult(4, 'e')), encodeModified(listOfChars("aaaabccaadeeee")));
    }

    static <A> List<RunLength<A>> encodeModified(List<A> list) {
        return new NestedFunction() {
            List<RunLength<A>> encode(List<A> list) {
                return list.matchVal(
                        List::nil,
                        (x, xs) -> encode(x, 1, xs)
                );
            }

            List<RunLength<A>> encode(A current, int count, List<A> list) {
                return list.matchVal(
                        () -> List.of(mult(count, current)),
                        (x, xs) -> x.equals(current) ?
                                encode(x, count + 1, xs) :
                                List.cons(count == 1 ? single(current) : mult(count, current), encode(list))
                );
            }
        }.encode(list);
    }

    static <A> RunLength<A> mult(int count, A elem) {
        return new RunLength<>(Either.right(Tuple.of(count, elem)));
    }

    static <A> RunLength<A> single(A elem) {
        return new RunLength<>(Either.left(elem));
    }

    static final class RunLength<A> extends TypeAlias<Either<A, Tuple<Integer, A>>> {
        private RunLength(Either<A, Tuple<Integer, A>> delegate) {
            super(delegate);
        }
    }

    /**
     * Problem 12
     * ==========
     *
     * Decode a run-length encoded list.
     *
     * Given a run-length code list generated as specified in problem 11. Construct its uncompressed version.
     *
     * Example in Haskell:
     *
     * P12> decodeModified [Multiple 4 'a',Single 'b',Multiple 2 'c',Multiple 2 'a',Single 'd',Multiple 4 'e']
     * "aaaabccaadeeee"
     */
    @Test
    public void problem12() {
        assertEquals(listOfChars("aaaabccaadeeee"), decodeModified(List.of(mult(4, 'a'), single('b'), mult(2, 'c'), mult(2, 'a'), single('d'), mult(4, 'e'))));
    }

    private static <A> List<A> decodeModified(List<RunLength<A>> list) {
        return new NestedFunction() {
            Trampoline<List<A>> decode(List<RunLength<A>> list) {
                return list.matchVal(
                        () -> Trampoline.done(List.nil()),
                        (head, tail) -> head.get().matchVal(
                                single -> Trampoline.suspend(() -> decode(1, single, tail)),
                                multiple -> Trampoline.suspend(() -> decode(multiple.first(), multiple.second(), tail))
                        )
                );
            }

            Trampoline<List<A>> decode(int count, A value, List<RunLength<A>> list) {
                return count == 0 ?
                        Trampoline.suspend(() -> decode(list)) :
                        Trampoline.suspend(() -> decode(count - 1, value, list).map(tail -> List.cons(value, tail)));
            }
        }.decode(list).run();
    }

    /**
     * Problem 13
     * ==========
     *
     * Run-length encoding of a list (direct solution).
     *
     * Implement the so-called run-length encoding data compression method directly.
     * I.e. don't explicitly create the sublists containing the duplicates, as in problem 9, but only count them.
     * As in problem P11, simplify the result list by replacing the singleton lists (1 X) by X.
     *
     * Example in Haskell:
     *
     * P13> encodeDirect "aaaabccaadeeee"
     * [Multiple 4 'a',Single 'b',Multiple 2 'c', Multiple 2 'a',Single 'd',Multiple 4 'e']
     */
    @Test
    public void problem13() {
        assertEquals(List.of(mult(4, 'a'), single('b'), mult(2, 'c'), mult(2, 'a'), single('d'), mult(4, 'e')), encodeModified(listOfChars("aaaabccaadeeee")));
    }

    static <A> List<RunLength<A>> encodeDirect(List<A> list) {
        // Kinda implemented already...
        return encodeModified(list);
    }

    /**
     * Problem 14
     * ==========
     *
     * Duplicate the elements of a list.
     *
     * Example in Haskell:
     *
     * > dupli [1, 2, 3]
     * [1,1,2,2,3,3]
     */
    @Test
    public void problem14() {
        assertEquals(List.of(1, 1, 2, 2, 3, 3), duplicate(List.of(1, 2, 3)));
    }

    private static <A> List<A> duplicate(List<A> list) {
        return new NestedFunction() {
            Trampoline<List<A>> duplicate(List<A> list) {
                return list.matchVal(
                        () -> Trampoline.done(List.nil()),
                        (x, xs) -> Trampoline.suspend(() -> duplicate(xs).map(tail -> List.cons(x, List.cons(x, tail))))
                );
            }
        }.duplicate(list).run();
    }

    /**
     * Problem 15
     * ==========
     *
     * Replicate the elements of a list a given number of times.
     *
     * Example in Haskell:
     *
     * > repli "abc" 3
     * "aaabbbccc"
     */
    @Test
    public void problem15() {
        assertEquals(listOfChars("aaabbbccc"), replicate(listOfChars("abc"), 3));
    }

    private static <A> List<A> replicate(List<A> list, int n) {
        return new NestedFunction() {
            Trampoline<List<A>> replicate(List<A> list, int n) {
                return list.matchVal(
                        () -> Trampoline.done(List.nil()),
                        (x, xs) -> Trampoline.suspend(() -> replicate(n, x, xs, n))
                );
            }

            Trampoline<List<A>> replicate(int i, A current, List<A> list, int n) {
                return i < 1 ?
                        Trampoline.suspend(() -> replicate(list, n)) :
                        Trampoline.suspend(() -> replicate(i - 1, current, list, n)).map(tail -> List.cons(current, tail));
            }
        }.replicate(list, n).run();
    }

    /**
     * Problem 16
     * ==========
     *
     * Drop every N'th element from a list.
     *
     * Example in Haskell:
     *
     * Prelude> dropEvery "abcdefghik" 3
     * "abdeghk"
     */
    @Test
    public void problem16() {
        assertEquals(listOfChars("abdeghk"), dropEvery(listOfChars("abcdefghik"), 3));
    }

    private <A> List<A> dropEvery(List<A> list, int n) {
        return new NestedFunction() {
            Trampoline<List<A>> dropEvery(List<A> list, int n, int countDown) {
                return list.matchVal(
                        () -> Trampoline.done(List.nil()),
                        (x, xs) -> countDown == 0 ?
                                Trampoline.suspend(() -> dropEvery(xs, n, n - 1)) :
                                Trampoline.suspend(() -> dropEvery(xs, n, countDown - 1).map(tail -> List.cons(x, tail)))
                );
            }
        }.dropEvery(list, n, n - 1).run();
    }

    /**
     * Problem 17
     * ==========
     *
     * Split a list into two parts; the length of the first part is given.
     *
     * Do not use any predefined predicates.
     *
     * Example in Haskell:
     *
     * Prelude> split "abcdefghik" 3
     * ("abc", "defghik")
     *
     */
    @Test
    public void problem17() {
        assertEquals(Tuple.of(listOfChars("abc"), listOfChars("defghik")), split(listOfChars("abcdefghik"), 3));
    }

    private static <A> Tuple<List<A>, List<A>> split(List<A> list, int n) {
        return new NestedFunction() {
            Trampoline<Tuple<List<A>, List<A>>> split(List<A> list, int n, List<A> buffer) {
                return list.matchVal(
                        () -> Trampoline.done(Tuple.of(reverse(buffer), list)),
                        (x, xs) -> n < 1 ?
                                Trampoline.done(Tuple.of(reverse(buffer), list)) :
                                Trampoline.suspend(() -> split(xs, n - 1, List.cons(x, buffer)))
                );
            }
        }.split(list, n, List.nil()).run();
    }

    /**
     * Problem 18
     * ==========
     * 
     * Extract a slice from a list.
     * 
     * Given two indices, i and k, the slice is the list containing the elements between the i'th and k'th element of 
     * the original list (both limits included). Start counting the elements with 1.
     * 
     * Example in Haskell:
     * 
     * Prelude> slice ['a','b','c','d','e','f','g','h','i','k'] 3 7
     * "cdefg"
     */
    @Test
    public void problem18() {
        assertEquals(listOfChars("cdefg"), slice(listOfChars("abcdefghik"), 3, 7));
    }
    
    private static <A> List<A> slice(List<A> list, int i, int k) {
        return new NestedFunction() {
            Trampoline<List<A>> slice(List<A> list, int i, int k) {
                return k < 1 ?
                        Trampoline.done(List.nil()) :
                        list.matchVal(
                                () -> Trampoline.done(List.nil()),
                                (x, xs) -> Trampoline.suspend(() -> slice(xs, i - 1, k - 1)).map(
                                        tail -> i > 1 ? tail : List.cons(x, tail))
                        );
            }
        }.slice(list, i, k).run();
    }

}

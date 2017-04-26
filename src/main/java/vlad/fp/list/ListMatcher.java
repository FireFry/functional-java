package vlad.fp.list;

import vlad.fp.either.Either;
import vlad.fp.maybe.Maybe;
import vlad.fp.tailrec.TailRec;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public final class ListMatcher<A, B> {

    private final Function<List<A>, Maybe<Either<ListMatcher<A, B>, B>>> nextOfDone;

    public static <A, B> ListMatcher<A, B> wrap(Function<List<A>, Maybe<Either<ListMatcher<A, B>, B>>> nextOfDone) {
        return new ListMatcher<>(nextOfDone);
    }

    private ListMatcher(Function<List<A>, Maybe<Either<ListMatcher<A, B>, B>>> nextOfDone) {
        this.nextOfDone = nextOfDone;
    }

    public Maybe<B> match(List<A> list) {
        return matchTailRec(list).eval();
    }

    private TailRec<Maybe<B>> matchTailRec(List<A> list) {
        return nextOfDone.apply(list).match(
                none -> TailRec.done(Maybe.none()),
                some -> some.value().matchVal(
                        next -> list.match(
                                nil -> TailRec.done(Maybe.none()),
                                cons -> TailRec.suspend(() -> next.matchTailRec(cons.tail()))
                        ),
                        result -> TailRec.done(Maybe.some(result))
                )
        );
    }

    public static <A, B> ListMatcher<A, B> when(boolean condition, Supplier<ListMatcher<A, B>> next) {
        return condition ? next.get() : wrap(list -> Maybe.none());
    }

    public static <A, B> ListMatcher<A, B> whenCons(BiFunction<A, List<A>, ListMatcher<A, B>> next) {
        return wrap(list -> list.matchVal(
                Maybe::none,
                (head, tail) -> Maybe.some(Either.left(next.apply(head, tail)))
        ));
    }

    public static <A, B> ListMatcher<A, B> whenCons(Function<A, ListMatcher<A, B>> next) {
        return whenCons((head, tail) -> next.apply(head));
    }

    public static <A, B> ListMatcher<A, B> whenCons(Supplier<ListMatcher<A, B>> next) {
        return whenCons((head, tail) -> next.get());
    }

    public static <A, B> ListMatcher<A, B> whenTail(Function<List<A>, B> tailMatcher) {
        return wrap(list -> list.match(
                nil -> Maybe.none(),
                cons -> Maybe.some(Either.right(tailMatcher.apply(cons.tail())))
        ));
    }

    public static <A, B> ListMatcher<A, B> whenNil(Supplier<B> supplier) {
        return wrap(list -> list.match(
                nil -> Maybe.some(Either.right(supplier.get())),
                cons -> Maybe.none()
        ));
    }

    public static <A, B> ListMatcher<A, B> whenOther(Supplier<B> result) {
        return then(result);
    }

    public static <A, B> ListMatcher<A, B> then(B result) {
        return then(() -> result);
    }

    public static <A, B> ListMatcher<A, B> then(Supplier<B> result) {
        return wrap(list -> Maybe.some(Either.right(result.get())));
    }

}

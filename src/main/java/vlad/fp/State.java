package vlad.fp;

import vlad.fp.tuple.Tuple;

import java.util.function.Function;

public final class State<S, A> {
    private final Function<S, Tuple<A, S>> run;

    public State(Function<S, Tuple<A, S>> run) {
        this.run = run;
    }

    public Function<S, Tuple<A, S>> run() {
        return run;
    }

    public static <S> State<S, S> get() {
        return new State<>(s -> Tuple.of(s, s));
    }

    public static <S> State<S, Unit> put(S s) {
        return new State<>(u -> Tuple.of(Unit.UNIT, s));
    }

    public static <S, A> State<S, A> join(State<S, State<S, A>> v1) {
        return new State<>(s1 -> v1.run().apply(s1).match((v2, s2) -> v2.run().apply(s2)));
    }
}

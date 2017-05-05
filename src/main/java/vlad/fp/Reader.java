package vlad.fp;

import vlad.fp.higher.Parametrized;

import java.util.function.Function;

public final class Reader<R, A> implements Parametrized<Parametrized<Reader, R>, A> {
    public static <R, A> Reader<R, A> lift(Parametrized<Parametrized<Reader, R>, A> fa) {
        return (Reader<R, A>) fa;
    }

    private final Function<R, A> run;

    public Reader(Function<R, A> run) {
        this.run = run;
    }

    public Function<R, A> run() {
        return run;
    }

    public <B> Reader<R, B> map(Function<A, B> f) {
        return new Reader<>(run.andThen(f));
    }

    public static <R> Reader<R, R> ask() {
        return new Reader<>(Function.identity());
    }

    public static <R, A> Reader<R, A> join(Reader<R, Reader<R, A>> r) {
        return new Reader<>(c -> r.run().apply(c).run().apply(c));
    }
}

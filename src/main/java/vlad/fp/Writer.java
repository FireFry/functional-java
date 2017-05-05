package vlad.fp;

import vlad.fp.lower.Monoid;

public final class Writer<W, A> {
    public final A value;
    public final W log;

    public Writer(A value, W log) {
        this.value = value;
        this.log = log;
    }

    public A value() {
        return value;
    }

    public W log() {
        return log;
    }

    public static <W> Writer<W, Unit> tell(W w) {
        return new Writer<>(Unit.UNIT, w);
    }

    public static <W, A> Writer<W, A> join(Monoid<W> M, Writer<W, Writer<W, A>> w) {
        return new Writer<>(w.value().value(), M.apply(w.log, w.value().log()));
    }

    public static <W, A> Writer<W, A> unit(Monoid<W> M, A a) {
        return new Writer<>(a, M.empty());
    }
}

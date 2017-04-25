package vlad.fp.list;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public final class Nil<A> extends List<A> {
    private static final Nil NIL = new Nil();

    @SuppressWarnings("unchecked")
    public static <A> Nil<A> get() {
        return NIL;
    }

    private Nil() {

    }

    @Override
    public <B> List<B> map(Function<A, B> function) {
        return List.nil();
    }

    @Override
    public <B> B match(Function<Nil<A>, B> nilCase, Function<Cons<A>, B> consCase) {
        return nilCase.apply(this);
    }

    @Override
    public <B> B matchVal(Supplier<B> nilCase, BiFunction<A, List<A>, B> consCase) {
        return nilCase.get();
    }

    @Override
    public String toString() {
        return "[]";
    }
}

package vlad.fp.lib;

import vlad.fp.lib.function.Function;
import vlad.fp.lib.function.Function2;
import vlad.fp.lib.function.Supplier;
import vlad.fp.lib.higher.Monad;
import vlad.fp.lib.higher.Parametrized;

public abstract class FPList<T> implements Parametrized<FPList, T> {

  public static final Monad<FPList> MONAD = FPListMonad.MONAD;

  public static <T> FPList<T> lift(Parametrized<FPList, T> par) {
    return (FPList<T>) par;
  }

  public static <T> Nil<T> nil() {
    return Nil.getInstance();
  }

  public static <T> Cons<T> cons(T head) {
    return cons(head, nil());
  }

  public static <T> Cons<T> cons(T head, FPList<T> tail) {
    return new Cons<>(head, tail);
  }

  public static <T> FPList<T> join(FPList<T> first, FPList<T> second) {
    return first.fold(
        () -> second,
        (x, xs) -> cons(x, join(xs, second))
    );
  }

  private FPList() {
    // sealed trait
  }

  public <R> FPList<R> map(Function<T, R> f) {
    return fold(
        FPList::nil,
        (head, tail) -> cons(f.apply(head), tail.map(f))
    );
  }

  public <R> FPList<R> flatMap(Function<T, FPList<R>> f) {
    return fold(
        FPList::nil,
        (x, xs) -> join(f.apply(x), xs.flatMap(f))
    );
  }

  public <R> R fold(Supplier<R> nilCase, Function2<T, FPList<T>, R> consCase) {
    return foldT(
        nil -> nilCase.apply(),
        cons -> consCase.apply(cons.head(), cons.tail())
    );
  }

  public <R> R foldT(Function<Nil<T>, R> nilCase, Function<Cons<T>, R> consCase) {
    switch (getType()) {
      case NIL:
        return nilCase.apply(asNil());
      case CONS:
        return consCase.apply(asCons());
      default:
        throw new AssertionError();
    }
  }

  public <R> R foldLeft(R initial, Function2<R, T, R> acc) {
    return foldLeftT(initial, acc).run();
  }

  private <R> Trampoline<R> foldLeftT(R initial, Function2<R, T, R> acc) {
    return fold(
        () -> Trampoline.done(initial),
        (head, tail) -> Trampoline.suspend(() -> tail.foldLeftT(acc.apply(initial, head), acc))
    );
  }

  public enum Type {
    NIL,
    CONS
  }

  public abstract Type getType();

  public Nil<T> asNil() {
    throw new AssertionError();
  }

  public Cons<T> asCons() {
    throw new AssertionError();
  }

  public static final class Nil<T> extends FPList<T> {
    private static final Nil INSTANCE = new Nil();

    @SuppressWarnings("unchecked")
    private static <T> Nil<T> getInstance() {
      return INSTANCE;
    }

    private Nil() {}

    @Override
    public Type getType() {
      return Type.NIL;
    }

    @Override
    public Nil<T> asNil() {
      return this;
    }
  }

  public static final class Cons<T> extends FPList<T> {
    private final T head;
    private final FPList<T> tail;

    private Cons(T head, FPList<T> tail) {
      this.head = head;
      this.tail = tail;
    }

    public T head() {
      return head;
    }

    public FPList<T> tail() {
      return tail;
    }

    @Override
    public Type getType() {
      return Type.CONS;
    }

    @Override
    public Cons<T> asCons() {
      return this;
    }
  }

  private enum FPListMonad implements Monad<FPList> {
    MONAD;

    @Override
    public <T, R> FPList<R> flatMap(Parametrized<FPList, T> fa, Function<T, Parametrized<FPList, R>> f) {
      return lift(fa).flatMap(x -> lift(f.apply(x)));
    }

    @Override
    public <T> Parametrized<FPList, T> pure(T x) {
      return cons(x);
    }
  }
}

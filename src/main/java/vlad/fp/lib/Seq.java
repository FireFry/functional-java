package vlad.fp.lib;

import vlad.fp.lib.function.Function;
import vlad.fp.lib.function.Function2;
import vlad.fp.lib.function.Supplier;
import vlad.fp.lib.higher.Monad;
import vlad.fp.lib.higher.Parametrized;

import java.util.Comparator;
import java.util.function.Predicate;

public abstract class Seq<T> implements Parametrized<Seq, T> {

  public static final Monad<Seq> MONAD = SeqMonad.MONAD;

  public static <T> Seq<T> lift(Parametrized<Seq, T> par) {
    return (Seq<T>) par;
  }

  public static <T> Nil<T> nil() {
    return Nil.getInstance();
  }

  public static <T> Cons<T> cons(T head) {
    return cons(head, nil());
  }

  public static <T> Cons<T> cons(T head, Seq<T> tail) {
    return new Cons<>(head, tail);
  }

  public Seq<T> sort(Comparator<T> c) {
    return fold(
        Seq::nil,
        (x, xs) -> {
          Seq<T> left = filter(y -> c.compare(x, y) <= 0);
          Seq<T> right = filter(y -> c.compare(x, y) > 0);
          return left.sort(c).join(right.sort(c));
        }
    );
  }

  private Seq() {
    // sealed trait
  }

  public <R> Seq<R> map(Function<T, R> f) {
    return fold(
        Seq::nil,
        (head, tail) -> cons(f.apply(head), tail.map(f))
    );
  }

  public <R> Seq<R> flatMap(Function<T, Seq<R>> f) {
    return flatMapT(f).run();
  }

  public <R> Trampoline<Seq<R>> flatMapT(Function<T, Seq<R>> f) {
    return fold(
        () -> Trampoline.done(Seq.nil()),
        (x, xs) -> Trampoline.suspend(() -> xs.flatMapT(f).map(tail -> f.apply(x).join(tail)))
    );
  }

  public <R> R fold(Supplier<R> nilCase, Function2<T, Seq<T>, R> consCase) {
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

  public Seq<T> join(Seq<T> other) {
    return joinT(other).run();
  }

  private Trampoline<Seq<T>> joinT(Seq<T> other) {
    return fold(
        () -> Trampoline.done(other),
        (x, xs) -> xs.joinT(other).map(tail -> cons(x, tail))
    );
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

  public Seq<T> filter(Predicate<T> predicate) {
    return flatMap(x -> predicate.test(x) ? Seq.cons(x) : Seq.nil());
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

  public static final class Nil<T> extends Seq<T> {
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

  public static final class Cons<T> extends Seq<T> {
    private final T head;
    private final Seq<T> tail;

    private Cons(T head, Seq<T> tail) {
      this.head = head;
      this.tail = tail;
    }

    public T head() {
      return head;
    }

    public Seq<T> tail() {
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

  private enum SeqMonad implements Monad<Seq> {
    MONAD;

    @Override
    public <T, R> Seq<R> flatMap(Parametrized<Seq, T> fa, Function<T, Parametrized<Seq, R>> f) {
      return lift(fa).flatMap(x -> lift(f.apply(x)));
    }

    @Override
    public <T> Parametrized<Seq, T> pure(T x) {
      return cons(x);
    }
  }
}

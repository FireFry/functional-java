package vlad.fp.lib;

import vlad.fp.lib.function.Function;
import vlad.fp.lib.function.Supplier;
import vlad.fp.lib.higher.Functor;
import vlad.fp.lib.higher.Parametrized;

public abstract class Free<F, T> implements Parametrized<Parametrized<Free, F>, T> {

  public static <F, T> Free<F, T> done(T value) {
    return Done(value);
  }

  public static <F, T> Free<F, T> liftF(Functor<F> F, Parametrized<F, T> value){
    return Suspend(F.map(value, Free::Done));
  }

  public static <F, T> Free<F, T> suspend(Parametrized<F, Free<F, T>> thunk) {
    return Suspend(thunk);
  }

  public static <F, T> Free<F, T> lift(Parametrized<Parametrized<Free, F>, T> f) {
    return (Free<F, T>) f;
  }

  private enum Type {
    DONE,
    SUSPEND,
    BIND_SUSPEND,
  }

  private Free() {
    // private constructor
  }

  protected abstract Type getType();

  private <S, R> R foldT(
      Function<Done<F, T>, R> doneCase,
      Function<Suspend<F, T>, R> suspendCase,
      Function<BindSuspend<F, S, T>, R> bindSuspendCase
  ) {
    switch (getType()) {
      case DONE:
        return doneCase.apply(asDone());
      case SUSPEND:
        return suspendCase.apply(asSuspend());
      case BIND_SUSPEND:
        return bindSuspendCase.apply(asBindSuspend());
      default:
        throw new AssertionError();
    }
  }

  public <R> Free<F, R> flatMap(Function<T, Free<F, R>> f) {
    return foldT(
        done -> BindSuspend(done, f),
        suspend -> BindSuspend(suspend, f),
        bindSuspend -> BindSuspend(bindSuspend.thunk, s -> bindSuspend.f.apply(s).flatMap(f))
    );
  }

  public final <G> Parametrized<G, T> foldMap(Functor<F> F, Monad<G> G, Natural<F, G> f){
    return resume(F).fold(
        left -> G.flatMap(f.apply(left), x -> x.foldMap(F, G, f)),
        right -> G.point(() -> right)
    );
  }

  public <R> R fold(Functor<F> F, Function<Parametrized<F, Free<F, T>>, R> suspendCase, Function<T, R> returnCase) {
    return resume(F).fold(suspendCase, returnCase);
  }

  public <R> Free<F, R> map(Function<T, R> f) {
    return flatMap(t -> Done(f.apply(t)));
  }

  public T run(Function<Parametrized<F, Free<F, T>>, Free<F, T>> f, Functor<F> F) {
    return Tailrec.run(resume(F), x -> x.fold(
        left -> Tailrec.next(f.apply(left).resume(F)),
        Tailrec::finish
    ));
  }

  private Either<Parametrized<F, Free<F, T>>, T> resume(Functor<F> F) {
    return Tailrec.run(this, x -> x.foldT(
        done -> Tailrec.finish(Either.right(done.value)),
        suspend -> Tailrec.finish(Either.left(suspend.thunk)),
        bindSuspend -> bindSuspend.thunk.foldT(
            doneA -> Tailrec.next(bindSuspend.f.apply(doneA.value)),
            suspendA -> Tailrec.finish(Either.left(
                F.map(suspendA.thunk, o -> o.flatMap(bindSuspend.f)))),
            bindSuspendA -> Tailrec.next(BindSuspend(bindSuspendA.thunk,
                s -> BindSuspend(bindSuspendA.f.apply(s), bindSuspend.f)))
        )
    ));
  }

  private static final class Done<F, T> extends Free<F, T> {
    private final T value;

    private Done(T value) {
      this.value = value;
    }

    @Override
    protected Type getType() {
      return Type.DONE;
    }

    @Override
    protected Done<F, T> asDone() {
      return this;
    }
  }

  private static <F, T> Done<F, T> Done(T value) {
    return new Done<>(value);
  }

  protected Done<F, T> asDone() {
    throw new AssertionError();
  }

  private static final class Suspend<F, T> extends Free<F, T> {
    private final Parametrized<F, Free<F, T>> thunk;

    private Suspend(Parametrized<F, Free<F, T>> thunk) {
      this.thunk = thunk;
    }

    @Override
    protected Type getType() {
      return Type.SUSPEND;
    }

    @Override
    protected Suspend<F, T> asSuspend() {
      return this;
    }
  }

  private static <F, T> Suspend<F, T> Suspend(Parametrized<F, Free<F, T>> thunk) {
    return new Suspend<>(thunk);
  }

  protected Suspend<F, T> asSuspend() {
    throw new AssertionError();
  }

  private static final class BindSuspend<F, S, T> extends Free<F, T> {
    private final Free<F, S> thunk;
    private final Function<S, Free<F, T>> f;

    private BindSuspend(Free<F, S> thunk, Function<S, Free<F, T>> f) {
      this.thunk = thunk;
      this.f = f;
    }

    @Override
    protected Type getType() {
      return Type.BIND_SUSPEND;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected BindSuspend<F, S, T> asBindSuspend() {
      return this;
    }
  }

  private static <F, S, T> BindSuspend<F, S, T> BindSuspend(
      Free<F, S> thunk,
      Function<S, Free<F, T>> f
  ) {
    return new BindSuspend<>(thunk, f);
  }

  protected <S> BindSuspend<F, S, T> asBindSuspend() {
    throw new AssertionError();
  }

  public static <S> Monad<Parametrized<Free, S>> freeMonad() {
    return new Monad<Parametrized<Free, S>>() {
      @Override
      public <T> Free<S, T> point(Supplier<T> a) {
        return Done(a.apply());
      }

      @Override
      public <T, R> Free<S, R> flatMap(Parametrized<Parametrized<Free, S>, T> fa, Function<T, Parametrized<Parametrized<Free, S>, R>> f) {
        return Free.lift(fa).flatMap(x -> Free.lift(f.apply(x)));
      }
    };
  }
}

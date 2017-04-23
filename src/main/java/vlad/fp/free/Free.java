package vlad.fp.free;

import vlad.fp.either.Either;
import vlad.fp.either.Left;
import vlad.fp.either.Right;
import vlad.fp.higher.Functor;
import vlad.fp.higher.Monad;
import vlad.fp.higher.Natural;
import vlad.fp.higher.Parametrized;
import vlad.fp.monads.FreeMonad;
import vlad.fp.tailrec.TailRec;

import java.util.function.Function;

public abstract class Free<F, A> implements Parametrized<Parametrized<Free, F>, A> {

  public static <F, A> Free<F, A> lift(Parametrized<Parametrized<Free, F>, A> par) {
    return (Free<F, A>) par;
  }

  public static <F, A> Free<F, A> done(A value) {
    return new Done<>(value);
  }

  public static <F, A> Free<F, A> liftF(Parametrized<F, A> value){
    return new Suspend<>(value);
  }

  public static <F, A> Free<F, A> suspend(Parametrized<F, Free<F, A>> value) {
    return liftF(value).flatMap(Function.identity());
  }

  Free() {

  }

  public abstract <S, B> B match(
      Function<Done<F, A>, B> doneCase,
      Function<Suspend<F, A>, B> suspendCase,
      Function<BindSuspend<F, S, A>, B> bindSuspendCase);

  public <B> Free<F, B> flatMap(Function<A, Free<F, B>> function) {
    return new BindSuspend<>(this, function);
  }

  public final <G> Parametrized<G, A> foldMap(Functor<F> F, Monad<G> G, Natural<F, G> N){
    return fold(F,
        left -> G.flatMap(G.flatMap(G.pure(left), N::apply), x -> x.foldMap(F, G, N)),
        G::pure
    );
  }

  public <B> B fold(
      Functor<F> F,
      Function<Parametrized<F, Free<F, A>>, B> suspendCase,
      Function<A, B> returnCase) {
    return resume(F).matchVal(suspendCase, returnCase);
  }

  public <B> Free<F, B> map(Function<? super A, B> function) {
    return flatMap(t -> done(function.apply(t)));
  }

  public A run(Functor<F> F, Function<Parametrized<F, Free<F, A>>, Free<F, A>> f) {
    return runTailRec(F, f).eval();
  }

  public TailRec<A> runTailRec(Functor<F> F, Function<Parametrized<F, Free<F, A>>, Free<F, A>> f) {
    return resume(F).matchVal(
        next -> TailRec.suspend(() -> f.apply(next).runTailRec(F, f)),
        TailRec::done
    );
  }

  private Either<Parametrized<F, Free<F, A>>, A> resume(Functor<F> F) {
    return resumeTailRec(F).eval();
  }

  private TailRec<Either<Parametrized<F, Free<F, A>>, A>> resumeTailRec(Functor<F> F) {
    return match(
        done -> TailRec.done(new Right<>(done.value())),
        suspend -> TailRec.done(new Left<>(F.map(suspend.value(), Free::done))),
        bindSuspend -> bindSuspend.prev().match(
            done2 -> TailRec.suspend(() -> bindSuspend.function().apply(done2.value()).resumeTailRec(F)),
            suspend2 -> TailRec.done(new Left<>(
                F.map(suspend2.value(), bindSuspend.function()))),
            bindSuspend2 -> TailRec.suspend(() -> bindSuspend2.flatMap(bindSuspend.function()).resumeTailRec(F))
        ));
  }

  public static <F> FreeMonad<F> monad() {
    return FreeMonad.get();
  }

}

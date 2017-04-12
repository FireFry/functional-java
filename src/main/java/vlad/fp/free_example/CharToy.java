package vlad.fp.free_example;

import vlad.fp.lib.Free;
import vlad.fp.lib.higher.Functor;
import vlad.fp.lib.higher.Parametrized;
import vlad.fp.lib.function.Function;
import vlad.fp.lib.function.Function2;

abstract class CharToy<A> implements Parametrized<CharToy, A> {
  public static <R> CharToy<R> lift(Parametrized<CharToy, R> l) {
    return (CharToy<R>) l;
  }

  public abstract <Z> Z fold(Function2<Character, A, Z> output, Function<A, Z> bell, Z done);

  public static Free<CharToy, Void> output(final char a){
    return Free.liftF(functor, new CharOutput<>(a, null));
  }
  public static Free<CharToy, Void> bell(){
    return Free.liftF(functor, new CharBell<Void>(null));
  }
  public static Free<CharToy, Void> done(){
    return Free.liftF(functor, new CharDone<Void>());
  }
  public static <A> Free<CharToy, A> pointed(final A a){
    return Free.done(a);
  }
  public abstract <B> CharToy<B> map(Function<A, B> f);
  private CharToy(){}

  public static final Functor<CharToy> functor =
    new Functor<CharToy>() {
      @Override
      public <X, Y> Parametrized<CharToy, Y> map(
          Parametrized<CharToy, X> fa,
          Function<X, Y> f) {
        return (lift(fa)).map(f);
      }
    };

  private static final class CharOutput<A> extends CharToy<A>{
    private final char a;
    private final A next;
    private CharOutput(final char a, final A next) {
      this.a = a;
      this.next = next;
    }

    @Override
    public <Z> Z fold(final Function2<Character, A, Z> output, final Function<A, Z> bell, final Z done) {
      return output.apply(a, next);
    }

    @Override
    public <B> CharToy<B> map(final Function<A, B> f) {
      return new CharOutput<>(a, f.apply(next));
    }
  }

  private static final class CharBell<A> extends CharToy<A> {
    private final A next;
    private CharBell(final A next) {
      this.next = next;
    }

    @Override
    public <Z> Z fold(final Function2<Character, A, Z> output, final Function<A, Z> bell, Z done) {
      return bell.apply(next);
    }

    @Override
    public <B> CharToy<B> map(final Function<A, B> f) {
      return new CharBell<>(f.apply(next));
    }
  }

  private static final class CharDone<A> extends CharToy<A> {
    @Override
    public <Z> Z fold(final Function2<Character, A, Z> output, final Function<A, Z> bell, final Z done) {
      return done;
    }

    @Override
    public <B> CharToy<B> map(final Function<A, B> f) {
      return new CharDone<>();
    }
  }
}

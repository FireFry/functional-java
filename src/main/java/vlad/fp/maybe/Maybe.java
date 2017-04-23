package vlad.fp.maybe;

import java.util.function.Function;
import java.util.function.Supplier;

public abstract class Maybe<A> {
  
  public static <A> Some<A> some(A value) {
    return new Some<>(value);
  }
  
  public static <A> None<A> none() {
    return None.get();
  }

  Maybe() {

  }

  public abstract <B> B match(Function<None<A>, B> noneCase, Function<Some<A>, B> someCase);

  public abstract <B> B matchVal(Supplier<B> noneCase, Function<A, B> someCase);

  public <R> Maybe<R> flatMap(Function<A, Maybe<R>> f) {
    return matchVal(Maybe::none, f);
  }

  public <R> Maybe<R> map(Function<A, R> f) {
    return flatMap(x -> Maybe.some(f.apply(x)));
  }

}

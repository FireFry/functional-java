package vlad.fp.lower;

public interface Monoid<T> extends Semigroup<T> {

  T empty();

}

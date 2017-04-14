package vlad.fp.lib;

public interface Monoid<T> extends Semigroup<T> {

  T empty();

}

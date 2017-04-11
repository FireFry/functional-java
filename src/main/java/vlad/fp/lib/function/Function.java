package vlad.fp.lib.function;

public interface Function<T, R> extends java.util.function.Function<T, R> {

  R apply(T arg);

}

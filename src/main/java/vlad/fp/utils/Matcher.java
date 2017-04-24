package vlad.fp.utils;

public class Matcher {

    public static <A> A unmatched() {
        throw new AssertionError();
    }

}

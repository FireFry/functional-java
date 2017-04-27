package vlad.fp;

import java.util.concurrent.atomic.AtomicLong;

public final class Random {

    private static final long MULTIPLIER = 0x5DEECE66DL;
    private static final long ADDEND = 0xBL;
    private static final long MASK = (1L << 48) - 1;

    private static final double DOUBLE_UNIT = 0x1.0p-53; // 1.0 / (1L << 53)

    private static final AtomicLong seedUniquifier = new AtomicLong(8682522807148012L);

    private final long seed;

    public Random() {
        this(seedUniquifier() ^ System.nanoTime());
    }

    public Random(long seed) {
        this.seed = seed;
    }

    private static long seedUniquifier() {
        for (;;) {
            long current = seedUniquifier.get();
            long next = current * 181783497276652981L;
            if (seedUniquifier.compareAndSet(current, next))
                return next;
        }
    }

    private static long initialScramble(long seed) {
        return (seed ^ MULTIPLIER) & MASK;
    }

    public Random next() {
        return new Random((seed * MULTIPLIER + ADDEND) & MASK);
    }

    private int nextInt(int bits) {
        return (int) (seed >>> (48 - bits));
    }

    public int getInt() {
        return nextInt(32);
    }

    public boolean getBoolean() {
        return nextInt(1) != 0;
    }

    public float getFloat() {
        return nextInt(24) / ((float) (1 << 24));
    }

    public double getDouble() {
        return (((long) (nextInt(26)) << 27) + nextInt(27)) * DOUBLE_UNIT;
    }

    public long getLong() {
        // it's okay that the bottom word remains signed.
        return ((long) (nextInt(32)) << 32) + nextInt(32);
    }

    public int getInt(int bound) {
        if (bound <= 0) {
            throw new IllegalArgumentException();
        }

        int r = nextInt(31);
        int m = bound - 1;
        if ((bound & m) == 0) { // i.e., bound is a power of 2
            r = (int) ((bound * (long) r) >> 31);
        } else {
            int u = r;
            r = u % bound;
            while (u - r + m < 0) {
                u = nextInt(31);
                r = u % bound;
            }
        }
        return r;
    }

}

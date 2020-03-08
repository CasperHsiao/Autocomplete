package arrayutils;

import java.util.Arrays;
import java.util.Comparator;
/**
 * Make sure to check out the interface for more method details:
 * @see ArraySearcher
 */
public class BinaryRangeSearcher<T, U> implements ArraySearcher<T, U> {
    private final Matcher<T, U> matcher;
    private final T[] allTerms;

    /**
     * Creates a BinaryRangeSearcher for the given array of items that matches items using the
     * Matcher matchUsing.
     *
     * First sorts the array in place using the Comparator sortUsing. (Assumes that the given array
     * will not be used externally afterwards.)
     *
     * Requires that sortUsing sorts the array such that for any possible reference item U,
     * calling matchUsing.match(T, U) on each T in the sorted array will result in all negative
     * values first, then all 0 values, then all positive.
     *
     * For example:
     * sortUsing lexicographic string sort: [  aaa,  abc,   ba,  bzb, cdef ]
     * matchUsing T is prefixed by U
     * matchUsing.match for prefix "b":     [   -1,   -1,    0,    0,    1 ]
     *
     * @throws IllegalArgumentException if array is null or contains null
     * @throws IllegalArgumentException if sortUsing or matchUsing is null
     */
    public static <T, U> BinaryRangeSearcher<T, U> forUnsortedArray(T[] array,
                                                                    Comparator<T> sortUsing,
                                                                    Matcher<T, U> matchUsing) {
        /*
        Tip: To reduce redundancy, you can let the BinaryRangeSearcher constructor throw some of
        the exceptions mentioned in this method's documentation. The caller doesn't care which
        method exactly causes the exception, as long as it's something that happens while
        executing this method.
        */

        if (sortUsing == null || matchUsing == null) {
            throw new IllegalArgumentException();
        }

        Arrays.sort(array, sortUsing);
        return new BinaryRangeSearcher<>(array, matchUsing);
    }

    /**
     * Requires that array is sorted such that for any possible reference item U,
     * calling matchUsing.match(T, U) on each T in the sorted array will result in all negative
     * values first, then all 0 values, then all positive.
     *
     * Assumes that the given array will not be used externally afterwards (and thus may directly
     * store and mutate the array).
     * @throws IllegalArgumentException if array is null or contains null
     * @throws IllegalArgumentException if matcher is null
     */
    protected BinaryRangeSearcher(T[] array, Matcher<T, U> matcher) {
        if (array == null) {
            throw new IllegalArgumentException();
        }
        if (matcher == null) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < array.length; i++) {
            if (array[i] == null) {
                throw new IllegalArgumentException();
            }
        }
        this.matcher = matcher;
        this.allTerms = array;

    }

    public MatchResult<T> findAllMatches(U target) {
        if (target == null) {
            throw new IllegalArgumentException();
        }
        int length = this.allTerms.length - 1;
        int start = binarySearchLow(target, 0, length, false);
        int end = 0;
        if (start != -1) {
            end = binarySearchHigh(target, start, length);
        } else {
            start = 0;
        }

        return new MatchResult<T>(this.allTerms, start, end);
    }

    private int binarySearchLow(U target, int low, int high, boolean contains) {
        while (low != high) {
            int mid = (high + low)/2;
            int result = this.matcher.match(this.allTerms[mid], target);
            if (result == 0) {
                contains = true;
                high = mid;
            } else if (result < 0) {
                low = mid + 1;
            } else {
                high = mid;
            }
        }
        if (contains || this.matcher.match(this.allTerms[high], target) == 0) {
            return high;
        }
        return -1;
    }

    private int binarySearchHigh(U target, int low, int high) {
        while (low != high) {
            int mid = (high + low)/2;
            int result = this.matcher.match(this.allTerms[mid], target);
            if (result == 0) {
                low = mid + 1;
            } else {
                high = mid;
            }
        }
        if (this.matcher.match(this.allTerms[high], target) == 0) {
            return high + 1;
        }
        return high;
    }

    public static class MatchResult<T> extends AbstractMatchResult<T> {
        final T[] array;
        final int start;
        final int end;

        /**
         * Use this constructor if there are no matching results.
         * (This lets us use Arrays.copyOfRange to make a new T[], which can be difficult to
         * acquire otherwise due to the way Java handles generics.)
         */
        protected MatchResult(T[] array) {
            this(array, 0, 0);
        }

        protected MatchResult(T[] array, int startInclusive, int endExclusive) {
            this.array = array;
            this.start = startInclusive;
            this.end = endExclusive;
        }

        @Override
        public int count() {
            return this.end - this.start;
        }

        @Override
        public T[] unsorted() {
            return Arrays.copyOfRange(this.array, this.start, this.end);
        }
    }
}

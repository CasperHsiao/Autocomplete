package autocomplete;


/**
 * This is currently the only implementation of the {@link Term} interface, which is why it's named
 * "default." (Having an interface with a single implementation is a little redundant, but we need
 * it to keep you from accidentally renaming things.)
 *
 * Make sure to check out the interface for method specifications.
 * @see Term
 */
public class DefaultTerm implements Term {
    private String query;
    private Long weight;

    /**
     * Initializes a term with the given query string and weight.
     * @throws IllegalArgumentException if query is null or weight is negative
     */
    public DefaultTerm(String query, long weight) {
        if (query == null || weight < 0) {
            throw new IllegalArgumentException();
        }
        this.query = query;
        this.weight = weight;
    }

    @Override
    public String query() {
        return this.query;
    }

    @Override
    public long weight() {
        return this.weight;
    }

    @Override
    public int queryOrder(Term that) {
        if (that == null) {
            throw new NullPointerException();
        }
        return this.query.compareTo(that.query());
    }

    @Override
    public int reverseWeightOrder(Term that) {
        if (that == null) {
            throw new NullPointerException();
        }
        return -this.weight.compareTo(that.weight());
    }

    @Override
    public int matchesPrefix(String prefix) {
        int result =  this.query.indexOf(prefix);
        if (result != 0) {
            return this.query.compareTo(prefix);
        }
        return result;

    }
}

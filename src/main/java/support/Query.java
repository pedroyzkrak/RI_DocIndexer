/**
 * Class built to save query information
 */
package support;

/**
 * @author Francisco Lopes 76406
 * @author Pedro Gusmão 77867
 */
public class Query {
    private final int id;
    private String querystr;

    public Query(int id, String str) {
        this.id = id;
        this.querystr = str;
    }

    public String getStr() {
        return querystr;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Query query = (Query) o;

        return id == query.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}

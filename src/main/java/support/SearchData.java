package support;

/**
 * @author Francisco Lopes 76406
 * @author Pedro Gusmão 77867
 * <p>
 * Saves search information
 */
public class SearchData implements Comparable<SearchData> {

    private Query query;
    private int docId;
    private double score;

    /**
     * Contructor that supports a query object and document ID related to the search
     *
     * @param query query object
     * @param docId document ID
     */
    public SearchData(Query query, int docId) {
        this.query = query;
        this.docId = docId;
        this.score = 0;
    }

    public Query getQuery() {
        return query;
    }

    public double getScore() {
        return score;
    }

    public int getDocId() {
        return docId;
    }

    public void setScore(double score) {
        this.score = score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SearchData that = (SearchData) o;

        if (docId != that.docId) return false;
        return query.equals(that.query);
    }

    @Override
    public int hashCode() {
        int result = query.hashCode();
        result = 31 * result + docId;
        return result;
    }

    /**
     * Comparable method to sort a list of SearchData objects by decreasing order of document score
     */
    @Override
    public int compareTo(SearchData sd) {
        if(this.score<sd.score)
            return 1;
        else if(sd.score<this.score)
            return -1;
        return 0;
    }
}

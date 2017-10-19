/**
 * 
 * Class built to save search information
 * 
 */
package support;

/**
 * @author Francisco Lopes 76406 
 * @author Pedro Gusmão 77867
 */
public class SearchData {
    private Query query;
    private int docId;
    private int score;

    public SearchData(Query query, int docId) {
        this.query = query;
        this.docId = docId;
        this.score = 0;
    }
    
    public Query getQuery() {
        return query;
    }

    public int getScore() {
        return score;
    }

    public int getDocId() {
        return docId;
    }

    public void setScore(int score) {
        this.score = score;
    }
    
    
    
}

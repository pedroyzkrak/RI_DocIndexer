/**
 * 
 * Class built to save document information
 * 
 */
package posting;

/**
 *
 * @author Francisco Lopes 76406 
 * @author Pedro Gusmão 77867
 */
public class Posting implements Comparable<Posting> {

    private final int docId;
    private int docFreq;
    private String term;
    
    /**
     * Contructor that supports the Document ID and Document Frequency for the tokens in the indexer
     * @param docId the ID of the document
     * @param docFreq the frequency of a token in documents
     */
    public Posting(int docId, int docFreq) {
        this.docId = docId;
        this.docFreq = docFreq;
    }
    /**
     * Contructor that saves information about the terms and their total frequency
     * @param term the term of the indexer
     * @param tDocFreq total document frequency of the term
     */
    public Posting(String term, int tDocFreq) {
        docId = -1;
        this.term = term;
        this.docFreq = tDocFreq;
    }

    public void setDocFreq(int docFreq) {
        this.docFreq = docFreq;
    }

    public int getDocId() {
        return docId;
    }

    public int getDocFreq() {
        return docFreq;
    }  
    
    public String getTerm() {
        return term;
    }
    
    /**
     * 
     * Comparable method to sort a list of Posting objects by decreasing order of document frequency
     * 
     */
    @Override
    public int compareTo(Posting p) {
        return (p.docFreq - docFreq);
    }

}

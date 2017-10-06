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
public class Posting {

    private final int docId;
    private int docFreq;
    
    /**
     * Contructor that supports the Document ID and Document Frequency for the tokens in the indexer
     * @param docId the ID of the document
     * @param docFreq the frequency of a token in documents
     */
    public Posting(int docId, int docFreq) {
        this.docId = docId;
        this.docFreq = docFreq;
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

}

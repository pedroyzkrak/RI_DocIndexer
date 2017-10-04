/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package posting;

/**
 *
 * @author Francisco Q Lopes
 */
public class Posting {

    private final int docId;
    private int docFreq;
    
    /**
     * Contructor that supports the DocId and DocFrequency for the tokens in the indexer
     * @param docId
     * @param docFreq 
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

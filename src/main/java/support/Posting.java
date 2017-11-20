package support;


import java.util.List;

/**
 * @author Francisco Lopes 76406
 * @author Pedro Gusmão 77867
 * <p>
 * Saves document information
 */
public class Posting implements Comparable<Posting> {

    private final int docId;
    private int termFreq, docFreq;
    private String term;
    private double weight;

    /**
     * Contructor that supports the Document ID and Document Frequency for the tokens in the indexer
     *
     * @param docId    the ID of the document
     * @param termFreq the frequency of the term in the document
     */
    public Posting(int docId, int termFreq) {
        this.docId = docId;
        this.termFreq = termFreq;
    }

    /**
     * Contructor that saves information about the terms and their total frequency
     *
     * @param term      the term of the indexer
     * @param tTermFreq total document frequency of the term
     */
    public Posting(String term, int tTermFreq) {
        docId = -1;
        this.term = term;
        this.termFreq = tTermFreq;
    }

    /**
     * Contructor that supports the Document ID and it's weight for the tokens in the indexer
     *
     * @param docId    the ID of the document
     * @param weight   the weight of the term in the document
     * @param termFreq the frequency of the term in the document
     * @param docFreq  number of documents that contain the term
     */
    public Posting(int docId, double weight, int termFreq, int docFreq) {
        this.docId = docId;
        this.weight = weight;
        this.termFreq = termFreq;
        this.docFreq = docFreq;
    }

    public void setTermFreq(int termFreq) {
        this.termFreq = termFreq;
    }

    public int getDocId() {
        return docId;
    }

    public int getTermFreq() {
        return termFreq;
    }

    public int getDocFreq() {
        return docFreq;
    }

    public double getWeight() {
        return weight;
    }

    public String getTerm() {
        return term;
    }

    /**
     * Comparable method to sort a list of Posting objects by decreasing order of document frequency
     */
    @Override
    public int compareTo(Posting p) {
        return (p.termFreq - termFreq);
    }

    /**
     * Checks if a certain Posting object has the same document ID as one of the Posting objects in the given list
     *
     * @param postingList list of postings
     * @param posting     posting object
     * @return true in case the given Posting object has the same ID as one of the elements of the list
     */
    public static boolean containsDocID(List<Posting> postingList, Posting posting) {
        for (Posting p : postingList) {
            if (p.getDocId() == posting.getDocId())
                return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Posting posting = (Posting) o;

        return docId == posting.docId;
    }

    @Override
    public int hashCode() {
        return docId;
    }
}

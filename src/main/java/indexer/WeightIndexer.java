package indexer;

import interfaces.Indexer;
import support.RankedData;
import tokenizer.SimpleTokenizer.Token;
import support.Posting;

import java.util.*;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;


/**
 * @author Francisco Lopes 76406
 * @author Pedro Gusmão 77867
 * <p>
 * Class for the weight indexer that it's main purpose is to apply tf-idf weights to terms and index them
 */
public class WeightIndexer implements Indexer {

    private final HashMap<String, LinkedList<Posting>> indexer;
    private HashMap<Integer, ArrayList<RankedData>> documentCache = new HashMap<>();
    private SimpleIndexer si = new SimpleIndexer();
    private String algorithm = "";

    /**
     * The constructor of the indexer that initializes the Map
     */
    public WeightIndexer() {
        this.indexer = new HashMap<>();
    }

    public WeightIndexer(String algorithm) {
        this.indexer = new HashMap<>();
        this.algorithm = algorithm;
    }

    /**
     * Indexes the tokens of a document to a hash map containing the terms and the corresponding document with the weight of said term
     *
     * @param tokenList The linked list of tokens
     * @param docId     The ID of the document
     */
    @Override
    public void index(LinkedList<Token> tokenList, int docId) {
        si.index(tokenList, docId);
    }

    public void weightIndex() {
        for (Map.Entry<String, LinkedList<Posting>> entry : si.entrySet()) {
            String term = entry.getKey();
            indexer.put(term, new LinkedList<>());
            double weight;
            for (Posting doc : entry.getValue()) {
                weight = (double) Math.round((  1 + Math.log10(doc.getTermFreq()) ) * 100) / 100;
                indexer.get(term).add(new Posting(doc.getDocId(), weight, doc.getTermFreq(), entry.getValue().size()));

                loadDocumentCache(doc.getDocId(), new RankedData(term, weight));
            }
        }
        si = null;

        // nomalize indexer terms
        normalizeIndexer();

        if (!algorithm.equals("rocchio"))
            documentCache = null;
    }

    private void normalizeIndexer() {
        for (Map.Entry<Integer, ArrayList<RankedData>> dc: documentCache.entrySet()) {
            int docID = dc.getKey();

            // nomalize term weights for the document cache
            normalizeTerms(dc.getValue(), calculateLength(dc.getValue()));

            // update indexer with nomalized weights
            for (RankedData rd : dc.getValue()) {
                Posting p = new Posting(docID, 0);
                int idx = indexer.get(rd.getTerm()).indexOf(p);
                indexer.get(rd.getTerm()).get(idx).setWeight(rd.getScore());
            }
        }
    }

    /**
     * Loads a document cache to know which terms occur in each document
     *
     * @param docID document ID
     * @param rd    RankedData object containing the term and it's weight in the document
     */
    private void loadDocumentCache(int docID, RankedData rd) {
        if (documentCache.containsKey(docID))
            documentCache.get(docID).add(rd);
        else {
            documentCache.put(docID, new ArrayList<>());
            documentCache.get(docID).add(rd);
        }
    }

    /**
     * Normalizes weights of the terms
     *
     * @param terms  the terms int the vector
     * @param length length of the vector
     */
    public static void normalizeTerms(List<RankedData> terms, double length) {
        for (RankedData rd : terms) {
            rd.setScore(Math.round(rd.getWeight() / length * 100.0) / 100.0);
        }
    }

    /**
     * @param vector query/document vector
     * @return vector length
     */
    public static double calculateLength(List<RankedData> vector) {
        double weight, length = 0;
        for (RankedData term : vector) {
            length += pow(term.getWeight(), 2);
        }
        weight = sqrt(length);
        return weight;
    }

    public HashMap<Integer, ArrayList<RankedData>> getDocumentCache() {
        return documentCache;
    }

    @Override
    public HashMap<String, LinkedList<Posting>> getIndexer() {
        return indexer;
    }

    @Override
    public void clear() {
        indexer.clear();
    }

    @Override
    public Iterable<Map.Entry<String, LinkedList<Posting>>> entrySet() {
        return indexer.entrySet();
    }
}

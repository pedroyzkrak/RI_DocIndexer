package indexer;

import interfaces.Indexer;
import tokenizer.SimpleTokenizer.Token;
import support.Posting;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;


/**
 * @author Francisco Lopes 76406
 * @author Pedro Gusmão 77867
 * <p>
 * Class for the weight indexer that it's main purpose is to apply tf-idf weights to terms and index them
 */
public class WeightIndexer implements Indexer {

    private final HashMap<String, LinkedList<Posting>> indexer;
    private SimpleIndexer si = new SimpleIndexer();

    /**
     * The constructor of the indexer that initializes the Map
     */
    public WeightIndexer() {
        this.indexer = new HashMap<>();
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
            }
        }
        si = null;
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

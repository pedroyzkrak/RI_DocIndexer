/**
 * 
 * Class built for the simple indexer
 * 
 */
package indexer;

import java.util.LinkedList;
import posting.Posting;
import Tokenizer.SimpleTokenizer.Token;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Francisco Lopes 76406 
 * @author Pedro Gusmão 77867
 * 
 * Class for the simple indexer that it's main purpose is to index multiple given tokens
 */

public class SimpleIndexer {

    private final HashMap<String, LinkedList<Posting>> indexer;

    /**
     * 
     * The constructor of the indexer that initializes the linked list
     * 
     */
    public SimpleIndexer() {
        this.indexer = new HashMap<>();
    }
    
    /**
     * Indexes the tokens of a document to a hash map containing the terms and the corresponding document with the frequency of said term
     * @param tokenList The linked list of tokens
     * @param docId The ID of the document  
     */
    public void index(LinkedList<Token> tokenList, int docId) {

        a:
        for (Token tok : tokenList) {

            // If token is not yet indexed
            if (!indexer.containsKey(tok.getSequence())) {
                indexer.put(tok.getSequence(), new LinkedList<>());
                indexer.get(tok.getSequence()).add(new Posting(docId, 1));
            } else {

                // check if the list contains a document linked to this token already
                for (Posting posting : indexer.get(tok.getSequence())) {
                    if (posting.getDocId() == docId) {
                        posting.setDocFreq(posting.getDocFreq() + 1);
                        continue a;
                    }
                }
                // add a new entry to the indexer for a new document that uses the token
                indexer.get(tok.getSequence()).add(new Posting(docId, 1));
            }
        }
    }
    
    /**
     * 
     * @return the entry set of the index
     */
    public Iterable<Map.Entry<String, LinkedList<Posting>>> entrySet() {
        return indexer.entrySet();
    }
    
    /**
     * clears the index
     */
    public void clear() {
        indexer.clear();
    }
    
    public List<String> getSingleTerms(int n) {
        LinkedList<String> singleTerms = new LinkedList<>();
        if (indexer.isEmpty()) {
            System.err.println("Indexer is empty.");
            return null;
        }
        else if (indexer.size() < n) {
            System.err.println("Value higher than indexer size.");
        }
        indexer.entrySet().stream().filter((entry) -> (entry.getValue().size() == 1)).forEachOrdered((entry) -> {
            singleTerms.add(entry.getKey());
        });
        
        Collections.sort(singleTerms);
        
        return singleTerms.subList(0, n);
    }
    
    public LinkedList<Posting> getHighestFrequency(int n) {
        LinkedList<Posting> termFreq = new LinkedList<>();
        int freq = 0;
        
        for (Map.Entry<String, LinkedList<Posting>> entry : indexer.entrySet()) {
            for(Posting post : entry.getValue()) {
                freq += post.getDocFreq();
                
            }
            termFreq.add(new Posting(entry.getKey(), freq));
        }
        return termFreq;
    }
}

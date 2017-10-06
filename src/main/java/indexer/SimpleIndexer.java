/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package indexer;

import java.util.LinkedList;
import posting.Posting;
import Tokenizer.SimpleTokenizer.Token;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Francisco Lopes 76406 
 * @author Pedro Gusmão 77867
 */

public class SimpleIndexer {

    private final HashMap<String, LinkedList<Posting>> indexer;

    public SimpleIndexer() {
        this.indexer = new HashMap<>();
    }
    
    /**
     * Indexes the tokens of a document
     * @param tokenList a list of tokens of a document
     * @param docId the ID of the document
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

}

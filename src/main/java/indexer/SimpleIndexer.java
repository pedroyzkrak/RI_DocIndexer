/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package indexer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.TreeMap;

/**
 *
 * @author Francisco Q Lopes
 */
public class SimpleIndexer {
    private TreeMap<String, ArrayList<Integer>> docTerms = new TreeMap<>();
    private TreeMap<String, Integer> docFreq = new TreeMap<>();
    
    
    public void indexer(LinkedList<String> tokenList, int docID) {
        
        for (String tok : tokenList) {
            if (!docTerms.containsKey(tok)) {
                docTerms.put(tok, new ArrayList<Integer>());
                docTerms.get(tok).add(docID);
                docFreq.put(tok, 1);
            }
            else if (docTerms.containsKey(tok) && !docTerms.get(tok).contains(docID)) {
                docTerms.get(tok).add(docID);
                docFreq.replace(tok, docFreq.get(tok), docFreq.get(tok)+1);
            }
        }
    }
}

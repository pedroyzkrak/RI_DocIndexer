/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package searcher;

import Tokenizer.SimpleTokenizer;
import Tokenizer.SimpleTokenizer.Token;
import indexer.SimpleIndexer;

import java.util.*;

import support.*;

/**
 *
 * @author Francisco Lopes 76406
 * @author Pedro Gusmão 77867
 *
 * A class for the Searcher
 *
 */
public class SimpleSearcher {

    public static List<SearchData> booleanSearchFirst(Query query, SimpleIndexer si) {
        SimpleTokenizer tkn = new SimpleTokenizer();
        tkn.tokenize(query.getStr(),"[a-zA-Z]{3,}",true,true);
        List<SearchData> searchList = new ArrayList<>();
        LinkedList<Token> wordsList = tkn.getTokens();
        Iterator<Token> wordsIt = wordsList.iterator();
        HashMap<String, LinkedList<Posting>> indexer = si.getIndexer();
        while (wordsIt.hasNext()) {
            String word = wordsIt.next().getSequence();
            if (indexer.containsKey(word)) {
               
                LinkedList<Posting> posting = indexer.get(word);
                Iterator<Posting> postingsIt = posting.iterator();
                
                while (postingsIt.hasNext()) {
                    
                    SearchData sd = new SearchData(query, postingsIt.next().getDocId());
                    
                    if (!searchList.contains(sd)) {
                        sd.setScore(1);
                        searchList.add(sd);
                    } else { //para termos diferentes pq supostamente n aparece + que uma vez um docId no mesmo termo
                        searchList.get(searchList.indexOf(sd)).setScore(searchList.get(searchList.indexOf(sd)).getScore() + 1); //rever
                    }
                }

            }
            
            
        }
        return searchList;
    }
}

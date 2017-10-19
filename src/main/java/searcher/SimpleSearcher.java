/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package searcher;

import Tokenizer.SimpleTokenizer;
import Tokenizer.SimpleTokenizer.Token;
import indexer.SimpleIndexer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import support.*;

/**
 *
 * @author Pedro
 */
public class SimpleSearcher {

    public List<String> wordsInQuery(String querystr) {
        List<String> words = Arrays.asList(querystr.split("[^\\w']+"));
        return words;

    }

    public List<SearchData> booleanSearchFirst(Query query, SimpleIndexer indexer) {
        SimpleTokenizer tkn = new SimpleTokenizer();
        tkn.tokenize(query.getStr(),"[a-zA-Z]{3,}",true,true);
        List<SearchData> searchList = new ArrayList<>();
        LinkedList<Token> wordsList = tkn.getTokens();
        Iterator<Token> wordsIt = wordsList.iterator();
        while (wordsIt.hasNext()) {
            String word = wordsIt.next().getSequence();
            if (indexer.getIndexer().containsKey(word)) {
               
                LinkedList<Posting> posting = indexer.getIndexer().get(word);
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

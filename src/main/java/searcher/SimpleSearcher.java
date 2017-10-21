/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package searcher;

import Tokenizer.SimpleTokenizer;
import Tokenizer.SimpleTokenizer.Token;
import indexer.IndexReader;
import indexer.SimpleIndexer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import save.SaveToFile;
import support.*;

/**
 * @author Francisco Lopes 76406
 * @author Pedro Gusmão 77867
 * <p>
 * A class for the Searcher
 */
public class SimpleSearcher {

    public static void readQueryFromFile(String fileName, String outputFile, String op) {

        try (BufferedReader in = new BufferedReader(new FileReader(fileName))) {
            String line;
            int id = 0;
            Query query;
            SimpleIndexer si = IndexReader.loadIndex("SaveIndex.txt");
            a:
            while ((line = in.readLine()) != null) {
                id++;
                query = new Query(id, line);

                switch (op) {
                    case "words":
                        SaveToFile.saveResults(booleanSearchFirst(query, si), outputFile);
                        break;

                    case "frequency":
                        SaveToFile.saveResults(booleanSearchSecond(query, si), outputFile);
                        break;

                    default:
                        System.err.println("Option not found.");
                        break a;
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static List<SearchData> booleanSearchFirst(Query query, SimpleIndexer si) {
        SimpleTokenizer tkn = new SimpleTokenizer();
        tkn.tokenize(query.getStr(), "[a-zA-Z]{3,}", true, true);
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

                    Posting pst = postingsIt.next();
                    SearchData sd = new SearchData(query, pst.getDocId());

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

    private static List<SearchData> booleanSearchSecond(Query query, SimpleIndexer si) {
        SimpleTokenizer tkn = new SimpleTokenizer();
        tkn.tokenize(query.getStr(), "[a-zA-Z]{3,}", true, true);
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

                    Posting pst = postingsIt.next();
                    SearchData sd = new SearchData(query, pst.getDocId());

                    if (!searchList.contains(sd)) {
                        sd.setScore(pst.getDocFreq());
                        searchList.add(sd);
                    } else { //para termos diferentes pq supostamente n aparece + que uma vez um docId no mesmo termo
                        searchList.get(searchList.indexOf(sd)).setScore(searchList.get(searchList.indexOf(sd)).getScore() + pst.getDocFreq()); //rever
                    }
                }

            }


        }
        return searchList;
    }

}

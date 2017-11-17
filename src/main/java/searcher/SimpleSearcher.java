/**
 * A class for the searcher
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
 * A class that implements Boolean retrieval methods
 */
public class SimpleSearcher {

    /**
     * Reads a file containing queries and saves the results to a file
     *
     * @param fileName   name of the file containing the queries
     * @param outputFile name of the files to save the results
     * @param op         type of boolean search (by 'words' or 'frequency')
     */
    public static void readQueryFromFile(String fileName, String outputFile, String op, SimpleIndexer si) {
        try (BufferedReader in = new BufferedReader(new FileReader(fileName))) {
            String line;
            int id = 0;
            Query query;
            a:
            while ((line = in.readLine()) != null) {
                id++;
                query = new Query(id, line);

                switch (op) {
                    case "words":
                        SaveToFile.saveResults(booleanSearchWord(query, si), outputFile);
                        break;

                    case "frequency":
                        SaveToFile.saveResults(booleanSearchFrequency(query, si), outputFile);
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

    /**
     * Performs a boolean search by the number of words in the query that appear in the document
     *
     * @param query the query object
     * @param si    the index
     * @return a list of SearchData objects containing information about the results of the query
     */
    private static List<SearchData> booleanSearchWord(Query query, SimpleIndexer si) {
        SimpleTokenizer tkn = new SimpleTokenizer();
        tkn.tokenize(query.getStr(), "[a-zA-Z]{3,}", true, true);
        List<SearchData> searchList = new ArrayList<>();
        LinkedList<Token> wordsList = tkn.getTokens();
        Iterator<Token> wordsIt = wordsList.iterator();
        HashMap<String, LinkedList<Posting>> indexer = si.getIndexer();
        int idx;
        SearchData searched_doc;

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
                        idx = searchList.indexOf(sd);
                        searched_doc = searchList.get(idx);
                        searched_doc.setScore(searched_doc.getScore() + 1); //rever
                    }
                }

            }


        }
        return searchList;
    }

    /**
     * Performs a boolean search by the total frequency of query words in the document
     *
     * @param query the query object
     * @param si    the index
     * @return a list of SearchData objects containing information about the results of the query
     */
    private static List<SearchData> booleanSearchFrequency(Query query, SimpleIndexer si) {
        SimpleTokenizer tkn = new SimpleTokenizer();
        tkn.tokenize(query.getStr(), "[a-zA-Z]{3,}", true, true);
        List<SearchData> searchList = new ArrayList<>();
        LinkedList<Token> wordsList = tkn.getTokens();
        Iterator<Token> wordsIt = wordsList.iterator();
        HashMap<String, LinkedList<Posting>> indexer = si.getIndexer();
        int idx;
        SearchData searched_doc;

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
                        idx = searchList.indexOf(sd);
                        searched_doc = searchList.get(idx);
                        searched_doc.setScore(searched_doc.getScore() + pst.getDocFreq()); //rever
                    }
                }

            }


        }
        return searchList;
    }

}

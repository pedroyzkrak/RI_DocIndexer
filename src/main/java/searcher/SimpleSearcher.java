package searcher;

import interfaces.Indexer;
import interfaces.Tokenizer;
import thesaurus.Thesaurus;
import tokenizer.SimpleTokenizer;
import tokenizer.SimpleTokenizer.Token;

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
     * Can also do query expansion by using a Thesaurus object (trained previously)
     *
     * @param fileName          name of the file containing the queries
     * @param outputFile        name of the files to save the results
     * @param outputMetricsFile name of file to save metrics
     * @param op                type of boolean search (by 'words' or 'frequency')
     * @param si                a SimpleIndexer object
     * @param thesaurus         thesaurus that contains word similarity to expand the query
     */
    @SuppressWarnings("Duplicates")
    public static void readQueryFromFile(String fileName, String outputFile, String outputMetricsFile, String op, Indexer si, Thesaurus thesaurus) {
        try (BufferedReader in = new BufferedReader(new FileReader(fileName))) {
            String line, queryTimes;
            int id = 0;
            double latency, median;
            long start, end, tStart = System.currentTimeMillis();
            ArrayList<Double> medianLatency = new ArrayList<>();
            Query query;
            List<SearchData> results;
            a:
            while ((line = in.readLine()) != null) {
                id++;

                start = System.currentTimeMillis();

                if (thesaurus != null) {
                    line = thesaurus.getExpandedQuery(line);
                }

                query = new Query(id, line);

                switch (op) {
                    case "words":
                        results = booleanSearchWord(query, si);
                        break;

                    case "frequency":
                        results = booleanSearchFrequency(query, si);
                        break;

                    default:
                        System.err.println("Option not found.");
                        break a;
                }

                end = System.currentTimeMillis();
                latency = (double) (end - start);
                medianLatency.add(latency);

                SaveToFile.saveResults(results, outputFile);

            }
            long tEnd = System.currentTimeMillis();

            Collections.sort(medianLatency);

            System.out.println("\tQuery Throughput: " + (double) Math.round((id / ((tEnd - tStart) / 1000.0)) * 10) / 10 + " queries per second");


            if (medianLatency.size() % 2 == 0) {
                median = (medianLatency.get(medianLatency.size() / 2) + medianLatency.get((medianLatency.size() / 2) + 1)) / 2;
                System.out.println("\tMedian query latency: " + median + " ms");
                queryTimes = "Query Throughput: " + (double) Math.round((id / ((tEnd - tStart) / 1000.0)) * 10) / 10 + " queries per second\n" +
                        "Median query latency: " + median + " ms\n";

            } else {
                System.out.println("\tMedian query latency: " + medianLatency.get(Math.round(medianLatency.size() / 2)) + " ms");
                queryTimes = "Query Throughput: " + (double) Math.round((id / ((tEnd - tStart) / 1000.0)) * 10) / 10 + " queries per second\n" +
                        "Median query latency: " + medianLatency.get(Math.round(medianLatency.size() / 2)) + " ms\n";
            }

            SaveToFile.saveMetrics(queryTimes, outputMetricsFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads a file containing queries and saves the results to a file
     *
     * @param fileName          name of the file containing the queries
     * @param outputFile        name of the file to save the results
     * @param outputMetricsFile name of file to save metrics
     * @param op                type of boolean search (by 'words' or 'frequency')
     * @param si                a SimpleIndexer object
     */
    public static void readQueryFromFile(String fileName, String outputFile, String outputMetricsFile, String op, Indexer si) {
        readQueryFromFile(fileName, outputFile, outputMetricsFile, op, si, null);
    }

    /**
     * Performs a boolean search by the number of words in the query that appear in the document
     *
     * @param query the query object
     * @param si    the index
     * @return a list of SearchData objects containing information about the results of the query
     */
    private static List<SearchData> booleanSearchWord(Query query, Indexer si) {
        Tokenizer tkn = new SimpleTokenizer();
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

                for (Posting pst : posting) {

                    SearchData sd = new SearchData(query, pst.getDocId());

                    if (!searchList.contains(sd)) {
                        sd.setScore(1);
                        searchList.add(sd);
                    } else {
                        idx = searchList.indexOf(sd);
                        searched_doc = searchList.get(idx);
                        searched_doc.setScore(searched_doc.getScore() + 1);
                    }
                }

            }


        }

        Collections.sort(searchList);

        return searchList;
    }

    /**
     * Performs a boolean search by the total frequency of query words in the document
     *
     * @param query the query object
     * @param si    the index
     * @return a list of SearchData objects containing information about the results of the query
     */
    private static List<SearchData> booleanSearchFrequency(Query query, Indexer si) {
        Tokenizer tkn = new SimpleTokenizer();
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

                for (Posting pst : posting) {

                    SearchData sd = new SearchData(query, pst.getDocId());

                    if (!searchList.contains(sd)) {
                        sd.setScore(pst.getTermFreq());
                        searchList.add(sd);
                    } else {
                        idx = searchList.indexOf(sd);
                        searched_doc = searchList.get(idx);
                        searched_doc.setScore(searched_doc.getScore() + pst.getTermFreq());
                    }
                }
            }
        }

        Collections.sort(searchList);

        return searchList;
    }

}

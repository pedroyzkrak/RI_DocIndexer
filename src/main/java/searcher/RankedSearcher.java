package searcher;

import interfaces.Tokenizer;
import tokenizer.SimpleTokenizer;
import interfaces.Indexer;
import save.SaveToFile;
import support.Posting;
import support.Query;
import support.RankedData;
import support.SearchData;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static java.lang.Math.*;

/**
 * @author Francisco Lopes 76406
 * @author Pedro Gusmão 77867
 * <p>
 * A class that implements a ranked retrieval method
 */
public class RankedSearcher {

    private static int collectionSize = -1;

    /**
     * Reads a file containing queries and saves the results to a file
     *
     * @param fileName   name of the file containing the queries
     * @param outputFile name of the files to save the results
     * @param wi         a WeightIndexer object
     */
    @SuppressWarnings("Duplicates")
    public static void readQueryFromFile(String fileName, String outputFile, Indexer wi) {
        try (BufferedReader in = new BufferedReader(new FileReader(fileName))) {
            String line, queryTimes;
            int id = 0;
            double latency, median;
            Query query;
            long tStart = System.currentTimeMillis(), start, end;
            ArrayList<Double> medianLatency = new ArrayList<>();
            List<SearchData> rankedList;
            while ((line = in.readLine()) != null) {
                id++;
                query = new Query(id, line);

                start = System.currentTimeMillis();

                rankedList = rankedRetrieval(query, wi);

                end = System.currentTimeMillis();
                latency = (double) (end - start);
                medianLatency.add(latency);

                SaveToFile.saveResults(rankedList, outputFile);
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

            SaveToFile.saveMetrics(queryTimes, "MetricsRanked.txt");


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Performs a ranked retrieval method by the weight in the terms
     *
     * @param query the query object
     * @param wi    the index
     * @return a list of SearchData objects containing information about the results of the query
     */
    static List<SearchData> rankedRetrieval(Query query, Indexer wi) {
        Tokenizer tkn = new SimpleTokenizer();
        tkn.tokenize(query.getStr(), "[a-zA-Z]{3,}", true, true);

        List<SearchData> searchList = new ArrayList<>();
        List<RankedData> queryTerms = new ArrayList<>();

        Iterator<SimpleTokenizer.Token> wordsIt = tkn.getTokens().iterator();

        HashMap<String, LinkedList<Posting>> indexer = wi.getIndexer();
        HashMap<Integer, LinkedList<RankedData>> search = new HashMap<>();

        // create a vector for the terms in the query and a data structure for the docIDs that contain such terms (map)
        while (wordsIt.hasNext()) {
            String word = wordsIt.next().getSequence();
            RankedData rd = new RankedData(word, 1);

            // update query vector
            queryVector(queryTerms, rd);

            // update docID query terms
            if (indexer.containsKey(word)) {
                docIdQueryTerms(indexer, search, word);
            }
        }

        // Calculate weight for every query term
        calculateQueryTermsWeight(indexer, queryTerms);

        // Normalize every query term weight
        normalizeTerms(queryTerms);

        // Normalize every document term weight
        for (Map.Entry<Integer, LinkedList<RankedData>> s : search.entrySet()) {

            // normalize each document term weight
            normalizeTerms(s.getValue());
        }

        // Aggregate a result list for each document score
        for (Map.Entry<Integer, LinkedList<RankedData>> s : search.entrySet()) {
            double score = 0;
            for (RankedData rd : queryTerms) {
                if (s.getValue().contains(rd)) {
                    double docScore = s.getValue().get(s.getValue().indexOf(rd)).getScore();
                    score += rd.getWeight() * docScore;
                }
            }
            SearchData sd = new SearchData(query, s.getKey());
            sd.setScore((double) round(score * 1000) / 1000);
            searchList.add(sd);
        }

        Collections.sort(searchList);

        return searchList;
    }

    /**
     * Normalizes weights of the terms
     * @param terms
     */
    private static void normalizeTerms(List<RankedData> terms) {
        for (RankedData rd : terms) {
            rd.setScore(rd.getWeight() / calculateLength(terms));
        }
    }

    /**
     * Checks if a word in the query is in the vector and update it
     * @param queryTerms query vector
     * @param rd RankedData object containing the query word
     */
    private static void queryVector(List<RankedData> queryTerms, RankedData rd) {
        if (queryTerms.contains(rd)) {
            RankedData term = queryTerms.get(queryTerms.indexOf(rd));
            term.setWeight(term.getWeight() + 1);
        } else
            queryTerms.add(rd);
    }

    /**
     * Updates the DocID data structure for the query terms and their weight in the documents
     * @param indexer the weighted index
     * @param search docID data structure
     * @param word query term
     */
    private static void docIdQueryTerms(HashMap<String, LinkedList<Posting>> indexer, HashMap<Integer, LinkedList<RankedData>> search, String word) {
        for (Posting posting : indexer.get(word)) {
            if (search.containsKey(posting.getDocId()))
                search.get(posting.getDocId()).add(new RankedData(word, posting.getWeight()));
            else {
                search.put(posting.getDocId(), new LinkedList<>());
                search.get(posting.getDocId()).add(new RankedData(word, posting.getWeight()));
            }
        }
    }

    private static void calculateQueryTermsWeight(HashMap<String, LinkedList<Posting>> indexer, List<RankedData> queryTerms) {
        for (RankedData qt : queryTerms) {
            if (indexer.containsKey(qt.getTerm())) {
                int df = indexer.get(qt.getTerm()).getFirst().getDocFreq();
                qt.setWeight(((1 + log10(qt.getWeight())) * log10(getCollectionSize() / df)));
            }
        }
    }

    /**
     *
     * @return Size of the document collection
     */
    private static int getCollectionSize() {
        if (collectionSize == -1) {
            List<String> fileNames = new ArrayList<>();
            try {
                DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get("cranfield"));
                for (Path path : directoryStream) {
                    fileNames.add(path.toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            collectionSize = fileNames.size();
        }
        return collectionSize;
    }

    /**
     *
     * @param vector query/document vector
     * @return vector length
     */
    private static double calculateLength(List<RankedData> vector) {
        double weight, length = 0;
        for (RankedData term : vector) {
            length += pow(term.getWeight(), 2);
        }
        weight = sqrt(length);
        return weight;
    }

}

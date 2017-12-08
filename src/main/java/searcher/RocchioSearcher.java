package searcher;

import interfaces.Indexer;
import save.SaveToFile;
import support.Posting;
import support.Query;
import support.RankedData;
import support.SearchData;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * @author Francisco Lopes 76406
 * @author Pedro Gusmão 77867
 * <p>
 * A class that implements the Rocchio algorithm
 */
public class RocchioSearcher {

    private static HashMap<Integer, ArrayList<RankedData>> documentCache = new HashMap<>();

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
            List<SearchData> queryVector;
            while ((line = in.readLine()) != null) {
                id++;
                query = new Query(id, line);

                start = System.currentTimeMillis();

                queryVector = RankedSearcher.rankedRetrieval(query, wi);

                end = System.currentTimeMillis();
                latency = (double) (end - start);
                medianLatency.add(latency);
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

    public static void loadDocumentCache(int docID, RankedData rd) {
        // update documentCache
    }

}

package searcher;

import interfaces.Indexer;
import save.SaveToFile;
import support.*;

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
    private static HashMap<Integer, List<Integer>> realRelevance;

    /**
     * Reads a file containing queries and saves the results to a file
     *
     * @param fileName   name of the file containing the queries
     * @param outputFile name of the files to save the results
     * @param op         type of relevance feedback ('explicit' or 'implicit')
     * @param wi         a WeightIndexer object
     */
    @SuppressWarnings("Duplicates")
    public static void readQueryFromFile(String fileName, String outputFile, String op, Indexer wi) {

        if (realRelevance == null && op.equals("explicit"))
            realRelevance = getRealRelevance();

        try (BufferedReader in = new BufferedReader(new FileReader(fileName))) {
            String line, queryTimes;
            int id = 0;
            double latency, median;
            Query query;
            long tStart = System.currentTimeMillis(), start, end;
            ArrayList<Double> medianLatency = new ArrayList<>();
            List<SearchData> queryResults, modifiedVector;

            while ((line = in.readLine()) != null) {
                id++;
                query = new Query(id, line);

                start = System.currentTimeMillis();

                queryResults = RankedSearcher.rankedRetrieval(query, wi);

                modifiedVector = rocchioAlgorithm(queryResults.subList(0, 10), id);


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

    /**
     * Performs the Rocchio Algorithm to update query terms from relevant documents
     *
     * @param results the query results
     * @param queryID ID of the current query
     * @return a modified query vector with more relevant results
     */
    private static List<SearchData> rocchioAlgorithm(List<SearchData> results, int queryID) {
        double alpha = 1, beta = 0.5, gama = 0.25;
        List<Integer> relevance = realRelevance.get(queryID), relevantDocs = getRelevantDocs(results, queryID), irrelevantDocs = getIrelevantDocs(results, queryID);

        // FAZER ROCCHIO

        return null;
    }

    /**
     * @param results Initial results obtained from the query
     * @param queryID Id of the query
     * @return Irrelevant docs obtained from the query
     */
    private static List<Integer> getRelevantDocs(List<SearchData> results, int queryID) {
        List<Integer> relevantDocs = new ArrayList<>();

        for (SearchData sd : results) {
            int docID = sd.getDocId();
            if (realRelevance.get(queryID).contains(docID))
                relevantDocs.add(docID);
        }

        return relevantDocs;
    }

    /**
     * @param results Initial results obtained from the query
     * @param queryID Id of the query
     * @return Irrelevant docs obtained from the query
     */
    private static List<Integer> getIrelevantDocs(List<SearchData> results, int queryID) {
        List<Integer> irrelevantDocs = new ArrayList<>();

        for (SearchData sd : results) {
            int docID = sd.getDocId();
            if (!realRelevance.get(queryID).contains(docID))
                irrelevantDocs.add(docID);
        }
        return irrelevantDocs;
    }


    /**
     * Loads a document cache to know which terms occur in each document
     *
     * @param docID document ID
     * @param rd    RankedData object containing the term and it's weight in the document
     */
    public static void loadDocumentCache(int docID, RankedData rd) {
        if (documentCache.containsKey(docID))
            documentCache.get(docID).add(rd);
        else {
            documentCache.put(docID, new ArrayList<>());
            documentCache.get(docID).add(rd);
        }
    }

    /**
     *
     * @return Relevant documents from the golden standard
     */
    private static HashMap<Integer, List<Integer>> getRealRelevance() {
        HashMap<Integer, List<Integer>> goldStandardDocs = new HashMap<>();
        try (BufferedReader in = new BufferedReader(new FileReader("cranfield.query.relevance.txt"))) {
            String line;
            while ((line = in.readLine()) != null) {
                String[] values = line.split(" ");
                int queryId = Integer.parseInt(values[0]);
                int docId = Integer.parseInt(values[1]);

                if (goldStandardDocs.containsKey(queryId)) {
                    goldStandardDocs.get(queryId).add(docId);
                } else {
                    goldStandardDocs.put(queryId, new ArrayList<>());
                    goldStandardDocs.get(queryId).add(docId);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return goldStandardDocs;
    }

}

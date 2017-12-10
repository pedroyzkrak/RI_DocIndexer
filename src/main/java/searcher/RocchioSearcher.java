package searcher;

import interfaces.Indexer;
import interfaces.Tokenizer;
import save.SaveToFile;
import support.*;
import tokenizer.SimpleTokenizer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static searcher.RankedSearcher.*;

/**
 * @author Francisco Lopes 76406
 * @author Pedro Gusmão 77867
 * <p>
 * A class that implements the Rocchio algorithm
 */
public class RocchioSearcher {

    private static HashMap<Integer, ArrayList<RankedData>> documentCache = new HashMap<>();
    private static HashMap<Integer, List<Integer>> realRelevance;
    private static double beta = 0.8, gama = 0.1;

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
            List<SearchData> queryResults, modifiedQueryResults;

            while ((line = in.readLine()) != null) {
                id++;
                query = new Query(id, line);

                start = System.currentTimeMillis();

                queryResults = RankedSearcher.rankedRetrieval(query, wi);

                modifiedQueryResults = rocchioRetrieval(query, wi, queryResults.subList(0, 10), op);

                end = System.currentTimeMillis();
                latency = (double) (end - start);
                medianLatency.add(latency);

                SaveToFile.saveResults(modifiedQueryResults, outputFile);

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

            switch (op) {
                case "explicit":
                    SaveToFile.saveMetrics(queryTimes, "MetricsExplicitRocchio.txt");
                    break;
                case "implicit":
                    SaveToFile.saveMetrics(queryTimes, "MetricsImplicitRocchio.txt");
                    break;
                default:
                    System.err.println("Invalid Rocchio feedback option.");
                    break;
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Performs a retrieval emethod utilizing the rocchio algorithm
     *
     * @param query   Query object that holds information about the query
     * @param wi      the weighted index
     * @param results initial results obtained from the query
     * @param op      type of relevance feedback ('explicit' or 'implicit')
     * @return a list of SearchData objects containing information about the results of the query through the rocchio algorithm
     */
    private static List<SearchData> rocchioRetrieval(Query query, Indexer wi, List<SearchData> results, String op) {
        Tokenizer tkn = new SimpleTokenizer();
        tkn.tokenize(query.getStr(), "[a-zA-Z]{3,}", true, true);

        List<SearchData> searchList = new ArrayList<>();
        List<RankedData> queryTerms = new ArrayList<>();

        Iterator<SimpleTokenizer.Token> wordsIt = tkn.getTokens().iterator();

        HashMap<String, LinkedList<Posting>> indexer = wi.getIndexer();
        HashMap<Integer, LinkedList<RankedData>> search = new HashMap<>();

        // create a vector for the terms in the query and a data structure for the docIDs that contain such terms (map)
        processQueryTerms(wordsIt, indexer, search, queryTerms);

        // Calculate weight for every query term
        calculateQueryTermsWeight(indexer, queryTerms);

        // Normalize every query term weight
        normalizeTerms(queryTerms, calculateLength(queryTerms));

        switch (op) {
            case "explicit":
                rocchioAlgorithmExplicit(results, queryTerms, query.getId());
                break;
            case "implicit":
                rocchioAlgorithmImplicit(results, queryTerms);
                break;
            default:
                System.err.println("Invalid Rocchio feedback option.");
                break;
        }

        // Normalize every document term weight
        for (Map.Entry<Integer, LinkedList<RankedData>> s : search.entrySet()) {

            // normalize each document term weight
            normalizeTerms(s.getValue(), calculateLength(s.getValue()));
        }

        // Aggregate a result list for each document score
        aggregateResults(search, queryTerms, searchList, query);

        Collections.sort(searchList);

        return searchList;
    }

    /**
     * Performs the Rocchio Algorithm to modify query terms
     * Modifies the original query vector
     *
     * @param results    initial results obtained from the query
     * @param queryID    ID of the current query
     * @param queryTerms original query vector
     */
    private static void rocchioAlgorithmExplicit(List<SearchData> results, List<RankedData> queryTerms, int queryID) {
        List<Integer> relevantDocs = getRelevantDocs(results, queryID), irrelevantDocs = getIrrelevantDocs(results, queryID);
        List<RankedData> relevantVector = new ArrayList<>(), irrelevantVector = new ArrayList<>();
        Iterator<Integer> relevantDocsIt = relevantDocs.iterator(), irrelevantDocsIt = irrelevantDocs.iterator();
        int relevantDocsSize = relevantDocs.size(), irrelevantDocsSize = irrelevantDocs.size();

        // get feedback vectors
        while (relevantDocsIt.hasNext() || irrelevantDocsIt.hasNext()) {
            if (relevantDocsIt.hasNext()) {
                int docIdR = relevantDocsIt.next();
                updateFeedBackVector(docIdR, relevantVector, relevantDocsSize, beta);
            }

            if (irrelevantDocsIt.hasNext()) {
                int docIdIr = irrelevantDocsIt.next();
                updateFeedBackVector(docIdIr, irrelevantVector, irrelevantDocsSize, gama);
            }
        }

        // sort feedback vectors in decreasing order
        Collections.sort(relevantVector);
        Collections.sort(irrelevantVector);

        // get first 5 (top 5) terms of each feedback vector
        if (relevantVector.size() < 5) {
            relevantVector = relevantVector.subList(0, relevantVector.size());
        } else {
            relevantVector = relevantVector.subList(0, 5);
        }
        if (irrelevantVector.size() < 5) {
            irrelevantVector = irrelevantVector.subList(0, irrelevantVector.size());
        } else {
            irrelevantVector = irrelevantVector.subList(0, 5);
        }

        // update query vector with new term weights
        Iterator<RankedData> relevantVectorIt = relevantVector.iterator();
        Iterator<RankedData> irrelevantVectorIt = irrelevantVector.iterator();
        while (relevantVectorIt.hasNext() || irrelevantVectorIt.hasNext()) {
            if (relevantVectorIt.hasNext()) {
                RankedData rdRe = relevantVectorIt.next();
                if (queryTerms.contains(rdRe)) {
                    int indexR = queryTerms.indexOf(rdRe);
                    queryTerms.get(indexR).setWeight(queryTerms.get(indexR).getWeight() + rdRe.getWeight());
                } else {
                    queryTerms.add(rdRe);
                }
            }

            if (irrelevantVectorIt.hasNext()) {
                RankedData rdIr = irrelevantVectorIt.next();
                if (queryTerms.contains(rdIr)) {
                    int indexR = queryTerms.indexOf(rdIr);
                    queryTerms.get(indexR).setWeight(queryTerms.get(indexR).getWeight() - rdIr.getWeight());
                }
            }
        }
    }

    /**
     * Performs an explicit Rocchio Algorithm to modify query terms
     * Modifies the original query vector
     *
     * @param results    initial results obtained from the query
     * @param queryTerms original query vector
     */
    private static void rocchioAlgorithmImplicit(List<SearchData> results, List<RankedData> queryTerms) {
        List<RankedData> vector = new ArrayList<>();
        int docsSize = results.size();

        // get feedback vectors
        for (SearchData sd: results) {
            int docId = sd.getDocId();
            updateFeedBackVector(docId, vector, docsSize, beta);
        }

        Collections.sort(vector);

        // get first 5 (top 5) terms of each feedback vector
        if (vector.size() < 5) {
            vector = vector.subList(0, vector.size());
        } else {
            vector = vector.subList(0, 5);
        }

        // update query vector with new term weights
        for (RankedData rd : vector) {
            if (queryTerms.contains(rd)) {
                int indexR = queryTerms.indexOf(rd);
                queryTerms.get(indexR).setWeight(queryTerms.get(indexR).getWeight() + rd.getWeight());
            } else {
                queryTerms.add(rd);
            }
        }

    }

    /**
     * Updates feedback vector
     *
     * @param docID            document ID
     * @param feedbackTerms    relevant/irrelevant document vector
     * @param feedbackTermSize number of relevant/irrelevant document vectors
     * @param feedbackWeight   feedback document weight
     */
    private static void updateFeedBackVector(int docID, List<RankedData> feedbackTerms, int feedbackTermSize, double feedbackWeight) {
        for (RankedData rd : documentCache.get(docID)) {
            if (feedbackTerms.contains(rd)) {
                int index = feedbackTerms.indexOf(rd);
                feedbackTerms.get(index).setWeight(feedbackTerms.get(index).getWeight() + (feedbackWeight / feedbackTermSize) * rd.getWeight());
            } else {
                rd.setWeight((feedbackWeight / feedbackTermSize) * rd.getWeight());
                feedbackTerms.add(rd);
            }
        }
    }

    /**
     * @param results initial results obtained from the query
     * @param queryID Id of the query
     * @return Relevant documents obtained from the query
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
     * @param results initial results obtained from the query
     * @param queryID Id of the query
     * @return Irrelevant documents obtained from the query
     */
    private static List<Integer> getIrrelevantDocs(List<SearchData> results, int queryID) {
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

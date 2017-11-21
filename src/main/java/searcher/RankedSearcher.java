package searcher;

import tokenizer.SimpleTokenizer;
import indexer.Indexer;
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

    private static int N = -1;

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
            double latency = 0, lat, median;
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
                lat = (double) (end - start);
                latency += lat;
                medianLatency.add(lat);

                SaveToFile.saveResults(rankedList, outputFile);
            }
            long tEnd = System.currentTimeMillis();

            Collections.sort(medianLatency);

            System.out.println("\tQuery Throughput: " + (double) Math.round((id / ((tEnd - tStart) / 1000.0)) * 10) / 10 + " queries per second");
            System.out.println("\tMean query latency: " + (double) Math.round((latency / id) * 10) / 10 + " ms");

            if (medianLatency.size() % 2 == 0) {
                median = (medianLatency.get(medianLatency.size() / 2) + medianLatency.get((medianLatency.size() / 2) + 1)) / 2;
                System.out.println("\tMedian query latency: " + median + " ms");
                queryTimes = "Query Throughput: " + (double) Math.round((id / ((tEnd - tStart) / 1000.0)) * 10) / 10 + " queries per second\n" +
                        "Mean query latency: " + (double) Math.round((latency / id) * 10) / 10 + " ms\n" +
                        "Median query latency: " + median + " ms\n";

            } else {
                System.out.println("\tMedian query latency: " + medianLatency.get(Math.round(medianLatency.size() / 2)) + " ms");
                queryTimes = "Query Throughput: " + (double) Math.round((id / ((tEnd - tStart) / 1000.0)) * 10) / 10 + " queries per second\n" +
                        "Mean query latency: " + (double) Math.round((latency / id) * 10) / 10 + " ms\n" +
                        "Median query latency: " + medianLatency.get(Math.round(medianLatency.size() / 2)) + " ms\n";
            }

            SaveToFile.saveMetrics(queryTimes, "MetricsRanked.txt");


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static List<SearchData> rankedRetrieval(Query query, Indexer wi) {
        SimpleTokenizer tkn = new SimpleTokenizer();
        tkn.tokenize(query.getStr(), "[a-zA-Z]{3,}", true, true);

        List<SearchData> searchList = new ArrayList<>();
        List<RankedData> queryTerms = new ArrayList<>();

        Iterator<SimpleTokenizer.Token> wordsIt = tkn.getTokens().iterator();

        HashMap<String, LinkedList<Posting>> indexer = wi.getIndexer();
        HashMap<Integer, LinkedList<RankedData>> search = new HashMap<>();

        // create a data set for the terms in the query (list) and for the docIDs that contain such terms (map)
        while (wordsIt.hasNext()) {
            String word = wordsIt.next().getSequence();
            RankedData rd = new RankedData(word, 1);
            if (queryTerms.contains(rd)) {
                RankedData term = queryTerms.get(queryTerms.indexOf(rd));
                term.setWeight(term.getWeight() + 1);
            } else
                queryTerms.add(rd);

            if (indexer.containsKey(word)) {
                for (Posting posting : indexer.get(word)) {
                    if (search.containsKey(posting.getDocId()))
                        search.get(posting.getDocId()).add(new RankedData(word, posting.getWeight()));
                    else {
                        search.put(posting.getDocId(), new LinkedList<>());
                        search.get(posting.getDocId()).add(new RankedData(word, posting.getWeight()));
                    }
                }
            }
        }

        // Calculate weight for every query term
        for (RankedData qt : queryTerms) {
            if (indexer.containsKey(qt.getTerm())) {
                int df = indexer.get(qt.getTerm()).getFirst().getDocFreq();
                qt.setWeight(((1 + log10(qt.getWeight())) * log10(getN() / df)));
            }
        }

        double queryLength = calculateLength(queryTerms);

        // Normalize every query term weight
        for (RankedData qt : queryTerms) {
            double weightNorm = qt.getWeight() / queryLength;
            qt.setScore(weightNorm);
        }

        // Normalize every document term weight
        for (Map.Entry<Integer, LinkedList<RankedData>> s : search.entrySet()) {
            // get doc length
            double length = calculateLength(s.getValue());

            // normalize each document term weight
            for (RankedData rd : s.getValue()) {
                rd.setScore(rd.getWeight() / sqrt(length));
            }
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

    private static int getN() {
        if (N == -1) {
            List<String> fileNames = new ArrayList<>();
            try {
                DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get("cranfield"));
                for (Path path : directoryStream) {
                    fileNames.add(path.toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            N = fileNames.size();
        }
        return N;
    }

    private static double calculateLength(List<RankedData> values) {
        double weight, length = 0;
        for (RankedData v : values) {
            length += pow(v.getWeight(), 2);
        }
        weight = sqrt(length);
        return weight;
    }

}

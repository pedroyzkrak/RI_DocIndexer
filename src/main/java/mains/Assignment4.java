package mains;

import corpusReader.CorpusReader;
import indexer.WeightIndexer;
import interfaces.Tokenizer;
import metrics.MetricsCalculation;
import searcher.RankedSearcher;
import searcher.RocchioSearcher;
import support.MetricsData;
import tokenizer.SimpleTokenizer;

import java.util.List;
import java.util.Map;

import static metrics.MetricsCalculation.*;
import static searcher.RocchioSearcher.getRealRelevance;

/**
 * Class that runs Assignment 4
 *
 * @author Francisco Lopes 76406
 * @author Pedro Gusmão 77867
 */
public class Assignment4 {
    public static void main() {
        
        // Variable initialization
        Tokenizer tokenizer = new SimpleTokenizer();
        WeightIndexer wi = new WeightIndexer("rocchio");

        long dirStart, dirEnd, indexStart, indexEnd;

        // Read Directory Section
        dirStart = System.currentTimeMillis();

        // build WeightIndexer
        CorpusReader.readAndProcessDir("cranfield", tokenizer, wi);

        dirEnd = System.currentTimeMillis();
        System.out.println("Read Dir time: " + (dirEnd - dirStart) / 1000.0 + " s\n");

        // READ QUERY SECTION
        // Read queries with weighted terms and rocchio implicit
        System.out.println("Rocchio Implicit Metrics");
        indexStart = System.currentTimeMillis();

        RocchioSearcher.readQueryFromFile("cranfield.queries.txt", "SaveResultsImplicitRocchio.txt", "implicit", wi);

        indexEnd = System.currentTimeMillis();
        System.out.println("Querying Time: " + (indexEnd - indexStart) / 1000.0 + " s\n");

        // Read queries with weighted terms and rocchio explicit
        System.out.println("Rocchio Explicit Metrics");
        indexStart = System.currentTimeMillis();

        RocchioSearcher.readQueryFromFile("cranfield.queries.txt", "SaveResultsExplicitRocchio.txt", "explicit", wi);

        indexEnd = System.currentTimeMillis();
        System.out.println("Querying Time: " + (indexEnd - indexStart) / 1000.0 + " s\n");

        // Read queries with weighted terms
        System.out.println("Ranked Metrics");
        indexStart = System.currentTimeMillis();

        RankedSearcher.readQueryFromFile("cranfield.queries.txt", "SaveResultsRanked.txt", wi);

        indexEnd = System.currentTimeMillis();
        System.out.println("Querying Time: " + (indexEnd - indexStart) / 1000.0 + " s\n");

        // METRICS CALCULATION FOR EACH QUERY SECTION
        Map<Integer, List<MetricsData>> baseSet = getRealRelevance(),
                rankedSet = parseResults("SaveResultsRanked.txt"),
                implicitRocchioSet = parseResults("SaveResultsImplicitRocchio.txt"),
                explicitRocchioSet = parseResults("SaveResultsExplicitRocchio.txt");

        MetricsCalculation calcRanked = new MetricsCalculation(), calcImplicitRocchio = new MetricsCalculation(), calcExplicitRocchio = new MetricsCalculation();
        String fileNameRanked = "MetricsRanked.txt", fileNameImplicit = "MetricsImplicitRocchio.txt", fileNameExplicit = "MetricsExplicitRocchio.txt";

        for (Map.Entry<Integer, List<MetricsData>> entry: baseSet.entrySet()) {
            int queryId = entry.getKey();
            List<MetricsData> rankedData = rankedSet.get(queryId),
                    implicitRocchioData = implicitRocchioSet.get(queryId),
                    explicitRocchioData = explicitRocchioSet.get(queryId),
                    baseData = entry.getValue();

            calcRanked.performQueryMetricCalculation(baseData, rankedData, queryId, fileNameRanked);
            calcImplicitRocchio.performQueryMetricCalculation(baseData, implicitRocchioData, queryId, fileNameImplicit);
            calcExplicitRocchio.performQueryMetricCalculation(baseData, explicitRocchioData, queryId, fileNameExplicit);
        }

        // SYSTEM METRIC RESULTS
        double size = baseSet.keySet().size();

        calcRanked.performSystemMetricCalculation(size, fileNameRanked);
        calcImplicitRocchio.performSystemMetricCalculation(size, fileNameImplicit);
        calcExplicitRocchio.performSystemMetricCalculation(size, fileNameExplicit);

    }
}

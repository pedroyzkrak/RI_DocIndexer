package mains;

import corpusReader.CorpusReader;
import indexer.WeightIndexer;
import interfaces.Tokenizer;
import metrics.MetricsCalculation;
import save.SaveToFile;
import searcher.RankedSearcher;
import searcher.RocchioSearcher;
import support.MetricsData;
import support.ValueHolder;
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
        /*
        // TESTING NDCG
        List<MetricsData> mdBase = new LinkedList<>(), mdTest = new LinkedList<>();

        mdBase.add(new MetricsData(1, 1));
        mdBase.add(new MetricsData(2, 2));
        mdBase.add(new MetricsData(3, 3));

        mdTest.add(new MetricsData(2, 1));
        mdTest.add(new MetricsData(3, 2));
        mdTest.add(new MetricsData(4, 1));
        mdTest.add(new MetricsData(5, 1));
        mdTest.add(new MetricsData(1, 123));

        System.out.println(calculateAverageNDCG(mdBase, mdTest));
        */



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

        ValueHolder rankedMAP = new ValueHolder(), rankedMRR = new ValueHolder(), rankedMAP10 = new ValueHolder(),
                implicitRocchioMAP = new ValueHolder(), implicitRocchioMRR = new ValueHolder(), implicitRocchioMAP10 = new ValueHolder(),
                explicitRocchioMAP = new ValueHolder(), explicitRocchioMRR = new ValueHolder(), explicitRocchioMAP10 = new ValueHolder();

        MetricsCalculation calcRanked = new MetricsCalculation(), calcImplicitRocchio = new MetricsCalculation(), calcExplicitRocchio = new MetricsCalculation();
        String fileNameRanked = "MetricsRanked.txt", fileNameImplicit = "MetricsImplicitRocchio.txt", fileNameExplicit = "MetricsExplicitRocchio.txt";

        for (Map.Entry<Integer, List<MetricsData>> entry: baseSet.entrySet()) {
            int queryId = entry.getKey();
            List<MetricsData> rankedData = rankedSet.get(queryId),
                    implicitRocchioData = implicitRocchioSet.get(queryId),
                    explicitRocchioData = explicitRocchioSet.get(queryId),
                    baseData = entry.getValue();

            performQueryMetricCalculation(baseData, rankedData, calcRanked, rankedMAP, rankedMAP10, rankedMRR, queryId, fileNameRanked);
            performQueryMetricCalculation(baseData, implicitRocchioData, calcImplicitRocchio, implicitRocchioMAP, implicitRocchioMAP10, implicitRocchioMRR, queryId, fileNameImplicit);
            performQueryMetricCalculation(baseData, explicitRocchioData, calcExplicitRocchio, explicitRocchioMAP, explicitRocchioMAP10, explicitRocchioMRR, queryId, fileNameExplicit);
        }

        // SYSTEM METRIC RESULTS
        double size = baseSet.keySet().size();

        performSystemMetricCalculation(calcRanked, rankedMAP.getValue(), rankedMAP10.getValue(), rankedMRR.getValue(), size, fileNameRanked);
        performSystemMetricCalculation(calcImplicitRocchio, implicitRocchioMAP.getValue(), implicitRocchioMAP10.getValue(), implicitRocchioMRR.getValue(), size, fileNameImplicit);
        performSystemMetricCalculation(calcExplicitRocchio, explicitRocchioMAP.getValue(), explicitRocchioMAP10.getValue(), explicitRocchioMRR.getValue(), size, fileNameExplicit);

    }
}

package mains;

import reader.CorpusReader;
import indexer.WeightIndexer;
import interfaces.Tokenizer;
import metrics.MetricsCalculation;
import searcher.RankedSearcher;
import searcher.RocchioSearcher;
import support.MetricsData;
import thesaurus.Thesaurus;
import tokenizer.SimpleTokenizer;

import java.io.FileNotFoundException;
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
        //main1();

        main2();
    }

    private static void main1() {

        // Variable initialization
        Tokenizer tokenizer = new SimpleTokenizer();
        WeightIndexer wi = new WeightIndexer("rocchio");

        long dirStart, dirEnd, indexStart, indexEnd;

        String collection = "cranfield",
                queriesFile = "cranfield.queries.txt",
                fileNameRankedResults = "SaveResultsRanked.txt",
                fileNameImplicitResults = "SaveResultsImplicitRocchio.txt",
                fileNameExplicitResults = "SaveResultsExplicitRocchio.txt",
                fileNameRankedMetrics = "MetricsRanked.txt",
                fileNameImplicitMetrics = "MetricsImplicitRocchio.txt",
                fileNameExplicitMetrics = "MetricsExplicitRocchio.txt";

        // Read Directory Section
        dirStart = System.currentTimeMillis();

        // build WeightIndexer
        CorpusReader.readAndProcessDir(collection, tokenizer, wi);

        dirEnd = System.currentTimeMillis();
        System.out.println("Read Dir time: " + (dirEnd - dirStart) / 1000.0 + " s\n");

        // READ QUERY SECTION
        // Read queries with weighted terms and rocchio implicit
        System.out.println("Rocchio Implicit Metrics");
        indexStart = System.currentTimeMillis();

        RocchioSearcher.readQueryFromFile(queriesFile, fileNameImplicitResults, fileNameImplicitMetrics, "implicit", wi);

        indexEnd = System.currentTimeMillis();
        System.out.println("Querying Time: " + (indexEnd - indexStart) / 1000.0 + " s\n");

        // Read queries with weighted terms and rocchio explicit
        System.out.println("Rocchio Explicit Metrics");
        indexStart = System.currentTimeMillis();

        RocchioSearcher.readQueryFromFile(queriesFile, fileNameExplicitResults, fileNameExplicitMetrics, "explicit", wi);

        indexEnd = System.currentTimeMillis();
        System.out.println("Querying Time: " + (indexEnd - indexStart) / 1000.0 + " s\n");

        // Read queries with weighted terms
        System.out.println("Ranked Metrics");
        indexStart = System.currentTimeMillis();

        RankedSearcher.readQueryFromFile(queriesFile, fileNameRankedResults, fileNameRankedMetrics, wi);

        indexEnd = System.currentTimeMillis();
        System.out.println("Querying Time: " + (indexEnd - indexStart) / 1000.0 + " s\n");

        // METRICS CALCULATION FOR EACH QUERY SECTION
        Map<Integer, List<MetricsData>> baseSet = getRealRelevance(),
                rankedSet = parseResults(fileNameRankedResults),
                implicitRocchioSet = parseResults(fileNameImplicitResults),
                explicitRocchioSet = parseResults(fileNameExplicitResults);

        MetricsCalculation calcRanked = new MetricsCalculation(), calcImplicitRocchio = new MetricsCalculation(), calcExplicitRocchio = new MetricsCalculation();

        for (Map.Entry<Integer, List<MetricsData>> entry : baseSet.entrySet()) {
            int queryId = entry.getKey();
            List<MetricsData> rankedData = rankedSet.get(queryId),
                    implicitRocchioData = implicitRocchioSet.get(queryId),
                    explicitRocchioData = explicitRocchioSet.get(queryId),
                    baseData = entry.getValue();

            calcRanked.performQueryMetricCalculation(baseData, rankedData, queryId, fileNameRankedMetrics);
            calcImplicitRocchio.performQueryMetricCalculation(baseData, implicitRocchioData, queryId, fileNameImplicitMetrics);
            calcExplicitRocchio.performQueryMetricCalculation(baseData, explicitRocchioData, queryId, fileNameExplicitMetrics);
        }

        // SYSTEM METRIC RESULTS
        double size = baseSet.keySet().size();

        calcRanked.performSystemMetricCalculation(size, fileNameRankedMetrics);
        calcImplicitRocchio.performSystemMetricCalculation(size, fileNameImplicitMetrics);
        calcExplicitRocchio.performSystemMetricCalculation(size, fileNameExplicitMetrics);
    }

    private static void main2() {
        // Variable initialization
        Tokenizer tokenizer = new SimpleTokenizer();
        WeightIndexer wi = new WeightIndexer("rocchio");

        Thesaurus t = null;
        try {
            t = new Thesaurus("aerodynamics_sentences.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        long dirStart, dirEnd, indexStart, indexEnd;
        String collection = "cranfield",
                queriesFile = "cranfield.queries.txt",
                fileNameRankedResults = "SaveResultsRankedThesaurus.txt",
                fileNameImplicitResults = "SaveResultsImplicitRocchioThesaurus.txt",
                fileNameExplicitResults = "SaveResultsExplicitRocchioThesaurus.txt",
                fileNameRankedMetrics = "MetricsRankedThesaurus.txt",
                fileNameImplicitMetrics = "MetricsImplicitRocchioThesaurus.txt",
                fileNameExplicitMetrics = "MetricsExplicitRocchioThesaurus.txt";


        // Read Directory Section
        dirStart = System.currentTimeMillis();

        // build WeightIndexer
        CorpusReader.readAndProcessDir(collection, tokenizer, wi);

        dirEnd = System.currentTimeMillis();
        System.out.println("Read Dir time: " + (dirEnd - dirStart) / 1000.0 + " s\n");

        // Read queries with weighted terms and rocchio explicit
        System.out.println("Rocchio Explicit with Thesaurus Metrics");
        indexStart = System.currentTimeMillis();

        RocchioSearcher.readQueryFromFile(queriesFile, fileNameExplicitResults, fileNameExplicitMetrics, "explicit", wi, t);

        indexEnd = System.currentTimeMillis();
        System.out.println("Querying Time: " + (indexEnd - indexStart) / 1000.0 + " s\n");

        // METRICS CALCULATION FOR EACH QUERY SECTION
        Map<Integer, List<MetricsData>> baseSet = getRealRelevance(),
                explicitRocchioSet = parseResults(fileNameExplicitResults);

        MetricsCalculation calcExplicitThesaurusRocchio = new MetricsCalculation();

        for (Map.Entry<Integer, List<MetricsData>> entry : baseSet.entrySet()) {
            int queryId = entry.getKey();
            List<MetricsData> explicitRocchioData = explicitRocchioSet.get(queryId),
                    baseData = entry.getValue();

            calcExplicitThesaurusRocchio.performQueryMetricCalculation(baseData, explicitRocchioData, queryId, fileNameExplicitMetrics);
        }

        // SYSTEM METRIC RESULTS
        double size = baseSet.keySet().size();

        calcExplicitThesaurusRocchio.performSystemMetricCalculation(size, fileNameExplicitMetrics);


    }
}

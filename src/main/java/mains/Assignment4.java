package mains;

import indexer.SimpleIndexer;
import reader.CorpusReader;
import indexer.WeightIndexer;
import interfaces.Tokenizer;
import metrics.MetricsCalculation;
import searcher.RankedSearcher;
import searcher.RocchioSearcher;
import searcher.SimpleSearcher;
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
        // Exercise 1
        System.out.println("Starting Rocchio Algorithm section...");
        //main1();

        // Exercise 2
        System.out.println("Starting Query Expansion section");
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

        System.out.println("Building WeightIndexer...");
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
        System.out.println("Parsing Results...");
        Map<Integer, List<MetricsData>> baseSet = getRealRelevance(),
                rankedSet = parseResults(fileNameRankedResults),
                implicitRocchioSet = parseResults(fileNameImplicitResults),
                explicitRocchioSet = parseResults(fileNameExplicitResults);

        MetricsCalculation calcRanked = new MetricsCalculation(), calcImplicitRocchio = new MetricsCalculation(), calcExplicitRocchio = new MetricsCalculation();

        System.out.println("Calculating Query Metrics...");
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

        System.out.println("Calculating System Metrics...");
        calcRanked.performSystemMetricCalculation(size, fileNameRankedMetrics);
        calcImplicitRocchio.performSystemMetricCalculation(size, fileNameImplicitMetrics);
        calcExplicitRocchio.performSystemMetricCalculation(size, fileNameExplicitMetrics);
    }

    private static void main2() {
        // Variable initialization
        Tokenizer tokenizer = new SimpleTokenizer();
        WeightIndexer wi = new WeightIndexer("rocchio");
        SimpleIndexer si = new SimpleIndexer();
        int neighbours = 1;

        long dirStart, dirEnd, indexStart, indexEnd;
        String collection = "cranfield",
                model = "aerodynamics_sentences.txt",
                queriesFile = "cranfield.queries.txt",
                fileNameRankedResults = "SaveResultsRankedQE.txt",
                fileNameImplicitResults = "SaveResultsImplicitRocchioQE.txt",
                fileNameExplicitResults = "SaveResultsExplicitRocchioQE.txt",
                fileNameFrequencyResults = "SaveResultsFrequencyQE.txt",
                fileNameWordsResults = "SaveResultsWordsQE.txt",
                fileNameRankedMetrics = "MetricsRankedQE.txt",
                fileNameImplicitMetrics = "MetricsImplicitRocchioQE.txt",
                fileNameExplicitMetrics = "MetricsExplicitRocchioQE.txt",
                fileNameFrequencyMetrics = "MetricsFrequencyQE.txt",
                fileNameWordsMetrics = "MetricsWordQE.txt";

        Thesaurus thesaurus = null;
        try {
            thesaurus = new Thesaurus(model, queriesFile, neighbours);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        // Read Directory Section
        dirStart = System.currentTimeMillis();

        System.out.println("Building SimpleIndexer...");
        // build SimpleIndexer
        CorpusReader.readAndProcessDir(collection, tokenizer, si);

        System.out.println("Building WeightIndexer...");
        // build WeightIndexer
        CorpusReader.readAndProcessDir(collection, tokenizer, wi);

        dirEnd = System.currentTimeMillis();
        System.out.println("Indexing time: " + (dirEnd - dirStart) / 1000.0 + " s\n");


        // Read queries with weighted terms and rocchio implicit with query expansion
        System.out.println("Rocchio Implicit with Query Expansion Metrics");
        indexStart = System.currentTimeMillis();
        RocchioSearcher.readQueryFromFile(queriesFile, fileNameImplicitResults, fileNameImplicitMetrics, "implicit", wi, thesaurus);
        indexEnd = System.currentTimeMillis();
        System.out.println("Querying Time: " + (indexEnd - indexStart) / 1000.0 + " s\n");


        // Read queries with weighted terms and rocchio explicit with query expansion
        System.out.println("Rocchio Explicit with Query Expansion Metrics");
        indexStart = System.currentTimeMillis();
        RocchioSearcher.readQueryFromFile(queriesFile, fileNameExplicitResults, fileNameExplicitMetrics, "explicit", wi, thesaurus);
        indexEnd = System.currentTimeMillis();
        System.out.println("Querying Time: " + (indexEnd - indexStart) / 1000.0 + " s\n");


        // Read queries with weighted terms with query expansion
        System.out.println("Ranked with Query Expansion Metrics");
        indexStart = System.currentTimeMillis();
        RankedSearcher.readQueryFromFile(queriesFile, fileNameRankedResults, fileNameRankedMetrics, wi, thesaurus);
        indexEnd = System.currentTimeMillis();
        System.out.println("Querying Time: " + (indexEnd - indexStart) / 1000.0 + " s\n");


        // Read queries with word count terms with query expansion
        System.out.println("Word with Query Expansion Metrics");
        indexStart = System.currentTimeMillis();
        SimpleSearcher.readQueryFromFile(queriesFile, fileNameWordsResults, fileNameWordsMetrics, "words", si, thesaurus);
        indexEnd = System.currentTimeMillis();
        System.out.println("Querying Time: " + (indexEnd - indexStart) / 1000.0 + " s\n");


        // Read queries with frequency terms with query expansion
        System.out.println("Frequency with Query Expansion Metrics");
        indexStart = System.currentTimeMillis();
        SimpleSearcher.readQueryFromFile(queriesFile, fileNameFrequencyResults, fileNameFrequencyMetrics, "frequency", si, thesaurus);
        indexEnd = System.currentTimeMillis();
        System.out.println("Querying Time: " + (indexEnd - indexStart) / 1000.0 + " s\n");


        // METRICS CALCULATION FOR EACH QUERY SECTION
        System.out.println("Parsing Results...");
        Map<Integer, List<MetricsData>> baseSet = getRealRelevance(),
                rankedSet = parseResults(fileNameRankedResults),
                implicitRocchioSet = parseResults(fileNameImplicitResults),
                explicitRocchioSet = parseResults(fileNameExplicitResults),
                wordsSet = parseResults(fileNameWordsResults),
                frequencySet = parseResults(fileNameFrequencyResults);

        MetricsCalculation calcRanked = new MetricsCalculation(),
                calcImplicitRocchio = new MetricsCalculation(),
                calcExplicitRocchio = new MetricsCalculation(),
                calcWords = new MetricsCalculation(),
                calcFrequency = new MetricsCalculation();

        System.out.println("Calculating Query Metrics...");
        for (Map.Entry<Integer, List<MetricsData>> entry : baseSet.entrySet()) {
            int queryId = entry.getKey();

            List<MetricsData> rankedData = rankedSet.get(queryId),
                    implicitRocchioData = implicitRocchioSet.get(queryId),
                    explicitRocchioData = explicitRocchioSet.get(queryId),
                    wordsData = wordsSet.get(queryId),
                    frequencyData = frequencySet.get(queryId),
                    baseData = entry.getValue();

            calcRanked.performQueryMetricCalculation(baseData, rankedData, queryId, fileNameRankedMetrics);
            calcImplicitRocchio.performQueryMetricCalculation(baseData, implicitRocchioData, queryId, fileNameImplicitMetrics);
            calcExplicitRocchio.performQueryMetricCalculation(baseData, explicitRocchioData, queryId, fileNameExplicitMetrics);
            calcWords.performQueryMetricCalculation(baseData, wordsData, queryId, fileNameWordsMetrics);
            calcFrequency.performQueryMetricCalculation(baseData, frequencyData, queryId, fileNameFrequencyMetrics);
        }

        // SYSTEM METRIC RESULTS
        double size = baseSet.keySet().size();

        System.out.println("Calculating System Metrics...");
        calcRanked.performSystemMetricCalculation(size, fileNameRankedMetrics);
        calcImplicitRocchio.performSystemMetricCalculation(size, fileNameImplicitMetrics);
        calcExplicitRocchio.performSystemMetricCalculation(size, fileNameExplicitMetrics);
        calcWords.performSystemMetricCalculation(size, fileNameWordsMetrics);
        calcFrequency.performSystemMetricCalculation(size, fileNameFrequencyMetrics);
    }
}

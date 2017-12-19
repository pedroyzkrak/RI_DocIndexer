package mains;

import reader.CorpusReader;
import indexer.SimpleIndexer;
import indexer.WeightIndexer;
import interfaces.Tokenizer;
import metrics.MetricsCalculation;
import save.SaveToFile;
import searcher.RankedSearcher;
import searcher.SimpleSearcher;
import support.MetricsData;
import tokenizer.SimpleTokenizer;

import java.util.List;
import java.util.Map;

import static metrics.MetricsCalculation.*;
import static searcher.RocchioSearcher.getRealRelevance;

/**
 * Class that runs Assignment 3
 * @author Francisco Lopes 76406
 * @author Pedro Gusmão 77867
 */
public class Assignment3 {
    public static void main() {

        // Variable initialization
        Tokenizer tokenizer = new SimpleTokenizer();
        SimpleIndexer si = new SimpleIndexer();
        WeightIndexer wi = new WeightIndexer();

        long dirStart, dirEnd, indexStart, indexEnd;

        String collection = "cranfield",
                queriesFile = "cranfield.queries.txt",
                fileNameRankedResults = "SaveResultsRanked.txt",
                fileNameFrequencyResults = "SaveResultsFrequency.txt",
                fileNameWordsResults = "SaveResultsWords.txt",
                fileNameRankedMetrics = "MetricsRanked.txt",
                fileNameFrequencyMetrics = "MetricsFrequency.txt",
                fileNameWordsMetrics = "MetricsWord.txt";

        // Read Directory Section
        dirStart = System.currentTimeMillis();

        System.out.println("Building SimpleIndexer...");
        // build SimpleIndexer
        CorpusReader.readAndProcessDir(collection, tokenizer, si);

        System.out.println("Building WeightIndexer...");
        // build WeightIndexer
        CorpusReader.readAndProcessDir(collection, tokenizer, wi);

        dirEnd = System.currentTimeMillis();
        System.out.println("Read Dir time: " + (dirEnd - dirStart) / 1000.0 + " s\n");

        //Save indexes
        SaveToFile.saveIndex(wi, "SaveWeightIndex.txt");
        SaveToFile.saveIndex(si, "SaveIndex.txt");


        // READ QUERY SECTION
        // Read queries with weighted terms
        System.out.println("Ranked Metrics");
        indexStart = System.currentTimeMillis();
        RankedSearcher.readQueryFromFile(queriesFile, fileNameRankedResults, fileNameRankedMetrics, wi);
        indexEnd = System.currentTimeMillis();
        System.out.println("Querying Time: " + (indexEnd - indexStart) / 1000.0 + " s\n");


        // Read queries with word count terms
        System.out.println("Word Metrics");
        indexStart = System.currentTimeMillis();
        SimpleSearcher.readQueryFromFile(queriesFile, fileNameWordsResults, fileNameWordsMetrics,"words", si);
        indexEnd = System.currentTimeMillis();
        System.out.println("Querying Time: " + (indexEnd - indexStart) / 1000.0 + " s\n");


        // Read queries with frequency terms
        System.out.println("Frequency Metrics");
        indexStart = System.currentTimeMillis();
        SimpleSearcher.readQueryFromFile(queriesFile, fileNameFrequencyResults, fileNameFrequencyMetrics,"frequency", si);
        indexEnd = System.currentTimeMillis();
        System.out.println("Querying Time: " + (indexEnd - indexStart) / 1000.0 + " s\n");


        // METRICS CALCULATION FOR EACH QUERY SECTION
        System.out.println("Parsing Results...");
        Map<Integer, List<MetricsData>> baseSet = getRealRelevance(),
                rankedSet = parseResults(fileNameRankedResults),
                wordsSet = parseResults(fileNameWordsResults),
                frequencySet = parseResults(fileNameFrequencyResults);

        MetricsCalculation calcRanked = new MetricsCalculation(),
                calcWords = new MetricsCalculation(),
                calcFrequency = new MetricsCalculation();

        System.out.println("Calculating Query Metrics...");
        for (Map.Entry<Integer, List<MetricsData>> entry: baseSet.entrySet()) {
            int queryId = entry.getKey();

            List<MetricsData> rankedData = rankedSet.get(queryId),
                    wordsData = wordsSet.get(queryId),
                    frequencyData = frequencySet.get(queryId),
                    baseData = entry.getValue();

            calcRanked.performQueryMetricCalculation(baseData, rankedData, queryId, fileNameRankedMetrics);
            calcWords.performQueryMetricCalculation(baseData, wordsData, queryId, fileNameWordsMetrics);
            calcFrequency.performQueryMetricCalculation(baseData, frequencyData, queryId, fileNameFrequencyMetrics);
        }

        // SYSTEM METRIC RESULTS
        double size = baseSet.keySet().size();

        System.out.println("Calculating System Metrics...");
        calcRanked.performSystemMetricCalculation(size, fileNameRankedMetrics);
        calcWords.performSystemMetricCalculation(size, fileNameWordsMetrics);
        calcFrequency.performSystemMetricCalculation(size, fileNameFrequencyMetrics);
    }
}

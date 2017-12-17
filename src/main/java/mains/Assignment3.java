package mains;

import corpusReader.CorpusReader;
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

        // Read Directory Section
        dirStart = System.currentTimeMillis();

        // build SimpleIndexer
        CorpusReader.readAndProcessDir("cranfield", tokenizer, si);
        // build WeightIndexer
        CorpusReader.readAndProcessDir("cranfield", tokenizer, wi);

        dirEnd = System.currentTimeMillis();
        System.out.println("Read Dir time: " + (dirEnd - dirStart) / 1000.0 + " s\n");

        //Save indexes
        SaveToFile.saveIndex(wi, "SaveWeightIndex.txt");
        SaveToFile.saveIndex(si, "SaveIndex.txt");

        // READ QUERY SECTION
        // Read queries with weighted terms
        System.out.println("Ranked Metrics");
        indexStart = System.currentTimeMillis();

        RankedSearcher.readQueryFromFile("cranfield.queries.txt", "SaveResultsRanked.txt", wi);

        indexEnd = System.currentTimeMillis();
        System.out.println("Querying Time: " + (indexEnd - indexStart) / 1000.0 + " s\n");

        // Read queries with word count terms
        System.out.println("Word Metrics");
        indexStart = System.currentTimeMillis();

        SimpleSearcher.readQueryFromFile("cranfield.queries.txt", "SaveResultsWords.txt", "words", si);

        indexEnd = System.currentTimeMillis();
        System.out.println("Querying Time: " + (indexEnd - indexStart) / 1000.0 + " s\n");

        // Read queries with frequency terms
        System.out.println("Frequency Metrics");
        indexStart = System.currentTimeMillis();

        SimpleSearcher.readQueryFromFile("cranfield.queries.txt", "SaveResultsFrequency.txt", "frequency", si);

        indexEnd = System.currentTimeMillis();
        System.out.println("Querying Time: " + (indexEnd - indexStart) / 1000.0 + " s\n");

        // METRICS CALCULATION FOR EACH QUERY SECTION
        Map<Integer, List<MetricsData>> baseSet = getRealRelevance(),
                rankedSet = parseResults("SaveResultsRanked.txt"),
                wordsSet = parseResults("SaveResultsWords.txt"),
                frequencySet = parseResults("SaveResultsFrequency.txt");

        MetricsCalculation calcRanked = new MetricsCalculation(), calcWords = new MetricsCalculation(), calcFrequency = new MetricsCalculation();
        String fileNameRanked = "MetricsRanked.txt", fileNameFrequency = "MetricsFrequency.txt", fileNameWords = "MetricsWord.txt";

        for (Map.Entry<Integer, List<MetricsData>> entry: baseSet.entrySet()) {
            int queryId = entry.getKey();
            List<MetricsData> rankedData = rankedSet.get(queryId),
                    wordsData = wordsSet.get(queryId),
                    frequencyData = frequencySet.get(queryId),
                    baseData = entry.getValue();

            calcRanked.performQueryMetricCalculation(baseData, rankedData, queryId, fileNameRanked);
            calcWords.performQueryMetricCalculation(baseData, wordsData, queryId, fileNameWords);
            calcFrequency.performQueryMetricCalculation(baseData, frequencyData, queryId, fileNameFrequency);
        }

        // SYSTEM METRIC RESULTS
        double size = baseSet.keySet().size();

        calcRanked.performSystemMetricCalculation(size, fileNameRanked);
        calcWords.performSystemMetricCalculation(size, fileNameWords);
        calcFrequency.performSystemMetricCalculation(size, fileNameFrequency);

    }
}

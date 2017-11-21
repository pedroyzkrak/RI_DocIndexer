
import metrics.MetricsCalculation;
import support.MetricsData;
import tokenizer.SimpleTokenizer;
import indexer.IndexReader;
import indexer.Indexer;
import indexer.SimpleIndexer;
import indexer.WeightIndexer;
import save.SaveToFile;
import corpusReader.CorpusReader;
import searcher.RankedSearcher;
import searcher.SimpleSearcher;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static metrics.MetricsCalculation.*;

/**
 * @author Francisco Lopes 76406
 * @author Pedro Gusmão 77867
 */
public class Main {

    public static void main(String[] args) {

        // Variable initialization
        SimpleTokenizer tokenizer = new SimpleTokenizer();
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

        // SaveToFile.saveIndex(wi, "SaveWeightIndex.txt");

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

        SimpleSearcher.readQueryFromFile("cranfield.queries.txt", "SaveResultsFrequency.txt", "frequency", wi);

        indexEnd = System.currentTimeMillis();
        System.out.println("Querying Time: " + (indexEnd - indexStart) / 1000.0 + " s\n");

        Map<Integer, List<MetricsData>> baseSet = parseResults("cranfield.query.relevance.txt"),
                                        rankedSet = parseResults("SaveResultsRanked.txt"),
                                        wordsSet = parseResults("SaveResultsWords.txt"),
                                        frequencySet = parseResults("SaveResultsFrequency.txt");

        double rankedMAP = 0, rankedMRR = 0, wordsMAP = 0, wordsMRR = 0, frequencyMAP = 0, frequencyMRR = 0;

        for (Map.Entry<Integer, List<MetricsData>> entry: baseSet.entrySet()) {
            int queryId = entry.getKey();
            List<MetricsData> rankedData = rankedSet.get(queryId),
                                wordsData = wordsSet.get(queryId),
                                frequencyData = frequencySet.get(queryId),
                                baseData = entry.getValue();

            double precisionRanked = calculatePrecision(baseData, rankedData),
                    recallRanked = calculateRecall(baseData, rankedData),
                    rMAP = calculateMeanAveragePrecision(baseData, rankedData),
                    rMRR = calculateMRR(baseData, rankedData),

                    precisionWords = calculatePrecision(baseData, wordsData),
                    recallWords = calculateRecall(baseData, wordsData),
                    wMAP = calculateMeanAveragePrecision(baseData, wordsData),
                    wMRR = calculateMRR(baseData, wordsData),

                    precisionFrequency = calculatePrecision(baseData, frequencyData),
                    recallFrequency = calculateRecall(baseData, frequencyData),
                    fMAP = calculateMeanAveragePrecision(baseData, frequencyData),
                    fMRR = calculateMRR(baseData, frequencyData);

            rankedMAP += rMAP;
            rankedMRR += rMRR;

            wordsMAP += wMAP;
            wordsMRR += wMRR;

            frequencyMAP += fMAP;
            frequencyMRR += fMRR;

            SaveToFile.saveMetrics(precisionRanked, recallRanked, calculateF_Measure(precisionRanked, recallRanked), rMAP, rMRR, queryId, "MetricsRanked.txt");
            SaveToFile.saveMetrics(precisionWords, recallWords, calculateF_Measure(precisionWords, recallWords), wMAP, wMRR, queryId, "MetricsWord.txt");
            SaveToFile.saveMetrics(precisionFrequency, recallFrequency, calculateF_Measure(precisionFrequency, recallFrequency), fMAP, fMRR, queryId, "MetricsFrequency.txt");
        }

        double size = baseSet.keySet().size();

        SaveToFile.saveMetrics("Mean Average Precision: " + (double) Math.round(rankedMAP / size * 100000) / 100000 + "\n", "MetricsRanked.txt", false);
        SaveToFile.saveMetrics("Mean Reciprocal Rank: " + (double) Math.round(rankedMRR / size * 100000) / 100000 + "\n", "MetricsRanked.txt", false);

        SaveToFile.saveMetrics("Mean Average Precision: " + (double) Math.round(wordsMAP / size * 100000) / 100000 + "\n", "MetricsWord.txt", false);
        SaveToFile.saveMetrics("Mean Reciprocal Rank: " + (double) Math.round(wordsMRR / size * 100000) / 100000 + "\n", "MetricsWord.txt", false);

        SaveToFile.saveMetrics("Mean Average Precision: " + (double) Math.round(frequencyMAP / size * 100000) / 100000 + "\n", "MetricsFrequency.txt", false);
        SaveToFile.saveMetrics("Mean Reciprocal Rank: " + (double) Math.round(frequencyMRR / size * 100000) / 100000 + "\n", "MetricsFrequency.txt", false);



        // Previous Assignments

        /*
        // Question 4
        for (String term : indexer.getSingleTerms(10)) {
            System.out.println("Term: " + term);
        }

        for (Posting freq : indexer.getHighestFrequency(10))
            System.out.println("Term: " + freq.getTerm() + " DocFreq: " + freq.getTermFreq());
        */

        //SaveToFile.saveIndex(IndexReader.loadIndex("SaveIndex.txt"), "newIndex.txt");

        /*
        long tStart = System.currentTimeMillis();

        SimpleIndexer si = IndexReader.loadIndex("SaveIndex.txt");

        SimpleSearcher.readQueryFromFile("cranfield.queries.txt", "SaveResultsWords.txt", "words", si);
        long tEnd = System.currentTimeMillis();
        long tDelta = tEnd - tStart;
        double elapsedSeconds = tDelta / 1000.0;
        System.out.println("Elapsed Time: "+elapsedSeconds);

        tStart = System.currentTimeMillis();
        SimpleSearcher.readQueryFromFile("cranfield.queries.txt", "SaveResultsFrequency.txt", "frequency", si);
        tEnd = System.currentTimeMillis();
        tDelta = tEnd - tStart;
        elapsedSeconds = tDelta / 1000.0;
        System.out.println("Elapsed Time: "+elapsedSeconds);

        */

    }
}

package mains;

import corpusReader.CorpusReader;
import indexer.WeightIndexer;
import interfaces.Tokenizer;
import metrics.MetricsCalculation;
import save.SaveToFile;
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

        double rankedMAP = 0, rankedMRR = 0, rankedMAP10 = 0, implicitRocchioMAP = 0, implicitRocchioMRR = 0, implicitRocchioMAP10 = 0, explicitRocchioMAP = 0, explicitRocchioMRR = 0, explicitRocchioMAP10 = 0;

        MetricsCalculation calcRanked = new MetricsCalculation(), calcImplicitRocchio = new MetricsCalculation(), calcExplicitRocchio = new MetricsCalculation();

        for (Map.Entry<Integer, List<MetricsData>> entry: baseSet.entrySet()) {
            int queryId = entry.getKey();
            List<MetricsData> rankedData = rankedSet.get(queryId),
                    implicitRocchioData = implicitRocchioSet.get(queryId),
                    explicitRocchioData = explicitRocchioSet.get(queryId),
                    baseData = entry.getValue();

            double precisionRanked = calcRanked.calculatePrecision(baseData, rankedData),
                    rMAP10 = calculateAveragePrecision(baseData, rankedData,10),
                    recallRanked = calcRanked.calculateRecall(baseData, rankedData),
                    rMAP = calculateAveragePrecision(baseData, rankedData,-1),
                    rMRR = calculateMRR(baseData, rankedData),

                    precisionImplicitRocchio = calcImplicitRocchio.calculatePrecision(baseData, implicitRocchioData),
                    irMAP10 = calculateAveragePrecision(baseData, implicitRocchioData, 10),
                    recallImplicitRocchio = calcImplicitRocchio.calculateRecall(baseData, implicitRocchioData),
                    irMAP = calculateAveragePrecision(baseData, implicitRocchioData, -1),
                    irMRR = calculateMRR(baseData, implicitRocchioData),

                    precisionExplicitRocchio = calcExplicitRocchio.calculatePrecision(baseData, explicitRocchioData),
                    erMAP10 = calculateAveragePrecision(baseData, explicitRocchioData,10),
                    recallExplicitRocchio = calcExplicitRocchio.calculateRecall(baseData, explicitRocchioData),
                    erMAP = calculateAveragePrecision(baseData, explicitRocchioData, -1),
                    erMRR = calculateMRR(baseData, explicitRocchioData);

            rankedMAP += rMAP;
            rankedMAP10 += rMAP10;
            rankedMRR += rMRR;

            implicitRocchioMAP += irMAP;
            implicitRocchioMAP10 += irMAP10;
            implicitRocchioMRR += irMRR;

            explicitRocchioMAP += erMAP;
            explicitRocchioMAP10 += erMAP10;
            explicitRocchioMRR += erMRR;

            if (queryId == 1) {
                SaveToFile.saveMetrics("\nQuery | Precision |  MAP10  |  Recall | F1-Measure | Avg. Precision | Reciprocal Rank\n" +
                        "------|-----------|---------|---------|------------|----------------|-----------------\n", "MetricsRanked.txt", false);

                SaveToFile.saveMetrics("\nQuery | Precision |  MAP10  |  Recall | F1-Measure | Avg. Precision | Reciprocal Rank\n" +
                        "------|-----------|---------|---------|------------|----------------|-----------------\n", "MetricsImplicitRocchio.txt", false);

                SaveToFile.saveMetrics("\nQuery | Precision |  MAP10  |  Recall | F1-Measure | Avg. Precision | Reciprocal Rank\n" +
                        "------|-----------|---------|---------|------------|----------------|-----------------\n", "MetricsExplicitRocchio.txt", false);
            }

            SaveToFile.saveMetrics(precisionRanked, rMAP10, recallRanked, calculateF_Measure(precisionRanked, recallRanked), rMAP, rMRR, queryId, "MetricsRanked.txt");
            SaveToFile.saveMetrics(precisionImplicitRocchio, irMAP10, recallImplicitRocchio, calculateF_Measure(precisionImplicitRocchio, recallImplicitRocchio), irMAP, irMRR, queryId, "MetricsImplicitRocchio.txt");
            SaveToFile.saveMetrics(precisionExplicitRocchio, erMAP10, recallExplicitRocchio, calculateF_Measure(precisionExplicitRocchio, recallExplicitRocchio), erMAP, erMRR, queryId, "MetricsExplicitRocchio.txt");
        }

        // SYSTEM METRIC RESULTS
        double size = baseSet.keySet().size();
        double sysPrecisionRanked = calcRanked.getGlobalPrecisionTP() / calcRanked.getGlobalPrecisionRetrieved(),
                sysRecallRanked = calcRanked.getGlobalRecallTP() / (calcRanked.getGlobalRecallTP() + calcRanked.getGlobalRecallFN()),

                sysPrecisionImplicitRocchio = calcImplicitRocchio.getGlobalPrecisionTP() / calcImplicitRocchio.getGlobalPrecisionRetrieved(),
                sysRecallImplicitRocchio = calcImplicitRocchio.getGlobalRecallTP() / (calcImplicitRocchio.getGlobalRecallTP() + calcImplicitRocchio.getGlobalRecallFN()),

                sysPrecisionExplicitRocchio = calcExplicitRocchio.getGlobalPrecisionTP() / calcExplicitRocchio.getGlobalPrecisionRetrieved(),
                sysRecallExplicitRocchio = calcExplicitRocchio.getGlobalRecallTP() / (calcExplicitRocchio.getGlobalRecallTP() + calcExplicitRocchio.getGlobalRecallFN());

        SaveToFile.saveMetrics("\nMean Average Precision: " + (double) Math.round(rankedMAP / size * 10000) / 10000 + "\n", "MetricsRanked.txt", false);
        SaveToFile.saveMetrics("Mean Average Precision at Rank 10: " + (double) Math.round(rankedMAP10 / size * 10000) / 10000 + "\n", "MetricsRanked.txt", false);
        SaveToFile.saveMetrics("Mean Reciprocal Rank: " + (double) Math.round(rankedMRR / size * 10000) / 10000 + "\n", "MetricsRanked.txt", false);
        SaveToFile.saveMetrics("System Precision: " + (double) Math.round(sysPrecisionRanked * 10000) / 10000 + "\n", "MetricsRanked.txt", false);
        SaveToFile.saveMetrics("System Recall: " + (double) Math.round(sysRecallRanked * 10000) / 10000 + "\n", "MetricsRanked.txt", false);
        SaveToFile.saveMetrics("System F1-Measure: " + (double) Math.round(calculateF_Measure(sysPrecisionRanked, sysRecallRanked) * 10000) / 10000 + "\n", "MetricsRanked.txt", false);


        SaveToFile.saveMetrics("\nMean Average Precision: " + (double) Math.round(implicitRocchioMAP / size * 10000) / 10000 + "\n", "MetricsImplicitRocchio.txt", false);
        SaveToFile.saveMetrics("Mean Average Precision at Rank 10: " + (double) Math.round(implicitRocchioMAP10 / size * 10000) / 10000 + "\n", "MetricsImplicitRocchio.txt", false);
        SaveToFile.saveMetrics("Mean Reciprocal Rank: " + (double) Math.round(implicitRocchioMRR / size * 10000) / 10000 + "\n", "MetricsImplicitRocchio.txt", false);
        SaveToFile.saveMetrics("System Precision: " + (double) Math.round(sysPrecisionImplicitRocchio * 10000) / 10000 + "\n", "MetricsImplicitRocchio.txt", false);
        SaveToFile.saveMetrics("System Recall: " + (double) Math.round(sysRecallImplicitRocchio * 10000) / 10000 + "\n", "MetricsImplicitRocchio.txt", false);
        SaveToFile.saveMetrics("System F1-Measure: " + (double) Math.round(calculateF_Measure(sysPrecisionImplicitRocchio, sysRecallImplicitRocchio) * 10000) / 10000 + "\n", "MetricsImplicitRocchio.txt", false);

        SaveToFile.saveMetrics("\nMean Average Precision: " + (double) Math.round(explicitRocchioMAP / size * 10000) / 10000 + "\n", "MetricsExplicitRocchio.txt", false);
        SaveToFile.saveMetrics("Mean Average Precision at Rank 10: " + (double) Math.round(explicitRocchioMAP10 / size * 10000) / 10000 + "\n", "MetricsExplicitRocchio.txt", false);
        SaveToFile.saveMetrics("Mean Reciprocal Rank: " + (double) Math.round(explicitRocchioMRR / size * 10000) / 10000 + "\n", "MetricsExplicitRocchio.txt", false);
        SaveToFile.saveMetrics("System Precision: " + (double) Math.round(sysPrecisionExplicitRocchio * 10000) / 10000 + "\n", "MetricsExplicitRocchio.txt", false);
        SaveToFile.saveMetrics("System Recall: " + (double) Math.round(sysRecallExplicitRocchio * 10000) / 10000 + "\n", "MetricsExplicitRocchio.txt", false);
        SaveToFile.saveMetrics("System F1-Measure: " + (double) Math.round(calculateF_Measure(sysPrecisionExplicitRocchio, sysRecallExplicitRocchio) * 10000) / 10000 + "\n", "MetricsExplicitRocchio.txt", false);

    }
}

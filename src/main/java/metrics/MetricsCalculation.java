/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package metrics;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import save.SaveToFile;
import support.MetricsData;

/**
 * @author Francisco Lopes 76406
 * @author Pedro Gusmão 77867
 */
public class MetricsCalculation {

    private double globalPrecisionTP, globalPrecisionRetrieved, globalRecallTP, globalRecallFN,
            queryMAP, queryMAP10, queryMRR, queryNDCG;

    /**
     * Constructor to initialize global variables for system metrics calculation
     */
    public MetricsCalculation() {
        globalPrecisionTP = 0;
        globalPrecisionRetrieved = 0;
        globalRecallTP = 0;
        globalRecallFN = 0;
        queryMAP = 0;
        queryMAP10 = 0;
        queryMRR = 0;
        queryNDCG = 0;
    }

    /**
     * Parses query results from a given file that contains them
     *
     * @param fileName file that contains the results of the queries
     * @return a map with query ID as key and corresponding list with MetricsData objects containing Document ID and it's score
     */
    public static HashMap<Integer, List<MetricsData>> parseResults(String fileName) {
        HashMap<Integer, List<MetricsData>> queryInfo = new HashMap<>();
        try (BufferedReader in = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = in.readLine()) != null) {
                String[] field = line.split(" ");
                int queryId = Integer.parseInt(field[0]);
                int docId = Integer.parseInt(field[1]);
                double score = Double.parseDouble(field[2]);

                if (queryInfo.containsKey(queryId)) {
                    queryInfo.get(queryId).add(new MetricsData(docId, score));
                } else {
                    queryInfo.put(queryId, new ArrayList<>());
                    queryInfo.get(queryId).add(new MetricsData(docId, score));
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        queryInfo.forEach((key, value) -> Collections.sort(value));
        return queryInfo;
    }

    /**
     * Calculates query precisio
     *
     * @param mdBase gold standard results of a query
     * @param mdTest test results from a query
     * @return query precision
     */
    private double calculatePrecision(List<MetricsData> mdBase, List<MetricsData> mdTest) {
        double precision;
        int tp = 0;
        for (MetricsData d : mdBase) {
            if (mdTest.contains(d)) {
                tp++;
                globalPrecisionTP++;
            }
            globalPrecisionRetrieved += mdTest.size();

        }
        precision = (double) tp / mdTest.size();

        return Math.round(precision * 10000.0) / 10000.0;
    }

    /**
     * Calculates query recall
     *
     * @param mdBase gold standard results of a query
     * @param mdTest test results from a query
     * @return query recall
     */
    private double calculateRecall(List<MetricsData> mdBase, List<MetricsData> mdTest) {
        double recall;
        int fn = 0;
        int tp = 0;
        for (MetricsData d : mdBase) {
            if (!mdTest.contains(d)) {
                fn++;
                globalRecallFN++;
            } else {
                tp++;
                globalRecallTP++;
            }

        }
        recall = (double) tp / (tp + fn);

        return Math.round(recall * 10000.0) / 10000.0;
    }

    /**
     * Calculates Harmonic mean of recall and precision from a query (F1)
     *
     * @param precision query precision
     * @param recall    query recall
     * @return F1-Measure of the query
     */
    private static double calculateF_Measure(double precision, double recall) {
        return Math.round(((2 * recall * precision) / (recall + precision)) * 10000.0) / 10000.0;
    }

    /**
     * Calculates Average precision of a query
     *
     * @param mdBase gold standard results of a query
     * @param mdTest test results from a query
     * @param cap    number of first query results to consider for calculation, < 0 will consider all
     * @return average precision of the query
     */
    private static double calculateAveragePrecision(List<MetricsData> mdBase, List<MetricsData> mdTest, int cap) {
        double avg_prec = 0;
        int tp = 0;
        int counter = 0;
        if (cap > 0) {
            if (mdTest.size() < cap)
                mdTest = mdTest.subList(0, mdTest.size());
            else
                mdTest = mdTest.subList(0, cap);
        }
        for (MetricsData d : mdTest) {
            counter++;
            if (mdBase.contains(d)) {
                tp++;
                avg_prec += (double) tp / counter;
            }
        }
        return Math.round((avg_prec / tp) * 10000.0) / 10000.0;
    }

    /**
     * Calculates Reciprocal Rank of a query
     *
     * @param mdBase gold standard results of a query
     * @param mdTest test results from a query
     * @return reciprocal rank of the query
     */
    private static double calculateRR(List<MetricsData> mdBase, List<MetricsData> mdTest) {
        int index;
        double mmr = 0.0;
        index = mdTest.indexOf(mdBase.get(0));
        if (index >= 0) {
            mmr = (double) 1 / (index + 1);
        }
        return Math.round(mmr * 10000.0) / 10000.0;
    }

    /**
     * Calculates the average Normalized Discounted Cumulative Gain of a query
     *
     * @param mdBase gold standard results of a query
     * @param mdTest test results from a query
     * @return returns an average NDCG of the query
     */
    private static double calculateAverageNDCG(List<MetricsData> mdBase, List<MetricsData> mdTest) {
        double avg_ndcg = 0, relevance, realRelevance, idealRelevance, realDCG = 0, idealDCG = 0;
        List<Double> real_dcg_values = new ArrayList<>(), ideal_dcg_values = new ArrayList<>();
        List<MetricsData> actualRanking = new ArrayList<>(), perfectRanking;

        for (MetricsData md : mdTest) {
            if (mdBase.contains(md)) {
                int idx = mdBase.indexOf(md);
                relevance = mdBase.get(idx).getScore();
                actualRanking.add(new MetricsData(md.getDocId(), relevance));
            } else {
                actualRanking.add(new MetricsData(md.getDocId(), 0));
            }
        }

        perfectRanking = new ArrayList<>(actualRanking);
        Collections.sort(perfectRanking);

        Iterator<MetricsData> perfectIt = perfectRanking.iterator(), actualIt = actualRanking.iterator();
        double rank = 0;
        while (perfectIt.hasNext() && actualIt.hasNext()) {
            rank++;
            realRelevance = actualIt.next().getScore();
            idealRelevance = perfectIt.next().getScore();

            if (log2(rank) == 0) {
                realDCG += realRelevance;
                real_dcg_values.add(realDCG);
                idealDCG += idealRelevance;
                ideal_dcg_values.add(idealDCG);
            } else {
                realDCG += realRelevance / log2(rank);
                real_dcg_values.add(realDCG);
                idealDCG += idealRelevance / log2(rank);
                ideal_dcg_values.add(idealDCG);
            }
        }

        Iterator<Double> real_dcgIT = real_dcg_values.iterator(), ideal_dcgIt = ideal_dcg_values.iterator();
        while (real_dcgIT.hasNext() && ideal_dcgIt.hasNext()) {
            double a = real_dcgIT.next() / ideal_dcgIt.next();
            avg_ndcg += a;
        }

        return Math.round(avg_ndcg / mdTest.size() * 10000.0) / 10000.0;
    }

    /**
     * Performs metrics calculation for each query
     *
     * @param base     gold standard results
     * @param test     results obtained
     * @param queryId  ID of the current query
     * @param fileName name of file to save metrics
     */
    public void performQueryMetricCalculation(List<MetricsData> base, List<MetricsData> test, int queryId, String fileName) {
        double precision = calculatePrecision(base, test),
                map10 = calculateAveragePrecision(base, test, 10),
                recall = calculateRecall(base, test),
                map = calculateAveragePrecision(base, test, -1),
                mrr = calculateRR(base, test),
                ndcg = calculateAverageNDCG(base, test.subList(0, 10));

        queryMAP += map;
        queryMAP10 += map10;
        queryMRR += mrr;
        queryNDCG += ndcg;

        if (queryId == 1)
            SaveToFile.saveMetrics("\nQuery | Precision |  MAP10  |  Recall | F1-Measure | Avg. Precision | Reciprocal Rank |  NDCG  \n" +
                    "------|-----------|---------|---------|------------|----------------|-----------------|---------\n", fileName, false);

        SaveToFile.saveMetrics(precision, map10, recall, calculateF_Measure(precision, recall), map, mrr, ndcg, queryId, fileName);
    }

    /**
     * Performs metrics calculation of the system
     *
     * @param size     total number of queries
     * @param fileName name of the file to save metrics
     */
    public void performSystemMetricCalculation(double size, String fileName) {
        double systemPrecision = globalPrecisionTP / globalPrecisionRetrieved,
                systemRecall = globalRecallTP / (globalRecallTP + globalRecallFN);

        String systemMetric =
                "\nMean Average Precision: " + Math.round(queryMAP / size * 10000.0) / 10000.0 + "\n" +
                        "Mean Average Precision at Rank 10: " + Math.round(queryMAP10 / size * 10000.0) / 10000.0 + "\n" +
                        "Mean Reciprocal Rank: " + Math.round(queryMRR / size * 10000.0) / 10000.0 + "\n" +
                        "System Precision: " + Math.round(systemPrecision * 10000.0) / 10000.0 + "\n" +
                        "System Recall: " + Math.round(systemRecall * 10000.0) / 10000.0 + "\n" +
                        "System F1-Measure: " + Math.round(calculateF_Measure(systemPrecision, systemRecall) * 10000.0) / 10000.0 + "\n" +
                        "Average Normalized DCG: " + Math.round(queryNDCG / size * 10000.0) / 10000.0 + "\n";

        SaveToFile.saveMetrics(systemMetric, fileName, false);

    }

    /**
     * Calculates log base 2 of a given number
     *
     * @param number number to calculate log base 2
     * @return log base 2 of a given number
     */
    private static double log2(double number) {
        return Math.log10(number) / Math.log10(2);
    }

}

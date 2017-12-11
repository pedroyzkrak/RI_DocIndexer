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

import support.MetricsData;

/**
 * @author Francisco Lopes 76406
 * @author Pedro Gusmão 77867
 */
public class MetricsCalculation {

    private double globalPrecisionTP, globalPrecisionRetrieved, globalRecallTP, globalRecallFN;

    public MetricsCalculation() {
        globalPrecisionTP = 0;
        globalPrecisionRetrieved = 0;
        globalRecallTP = 0;
        globalRecallFN = 0;
    }

    public static HashMap<Integer, List<MetricsData>> parseResults(String fileName) {
        HashMap<Integer, List<MetricsData>> queryInfo = new HashMap<>();
        try (BufferedReader in = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = in.readLine()) != null) {
                String[] els = line.split(" ");
                int queryId = Integer.parseInt(els[0]);
                int docId = Integer.parseInt(els[1]);
                double score = Double.parseDouble(els[2]);

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

    public double calculatePrecision(List<MetricsData> mdBase, List<MetricsData> mdTest) {
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

        return (double) Math.round(precision * 10000) / 10000;
    }

    public double calculateRecall(List<MetricsData> mdBase, List<MetricsData> mdTest) {
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

        return (double) Math.round(recall * 10000) / 10000;
    }

    public static double calculateF_Measure(double precision, double recall) {
        return (double) Math.round(((2 * recall * precision) / (recall + precision)) * 10000) / 10000;
    }

    public static double calculateAveragePrecision(List<MetricsData> mdBase, List<MetricsData> mdTest, int cap) {
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
        return (double) Math.round((avg_prec / tp) * 10000) / 10000;
    }

    public static double calculateMRR(List<MetricsData> mdBase, List<MetricsData> mdTest) {
        int index;
        double mmr = 0.0;
        index = mdTest.indexOf(mdBase.get(0));
        if (index > 0) {
            mmr = (double) 1 / (index + 1);
        }
        return (double) Math.round(mmr * 10000) / 10000;
    }

    public static double calculateAverageNDCG(List<MetricsData> mdBase, List<MetricsData> mdTest) {
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
            System.out.println(realDCG + " " + idealDCG + " -> " + realDCG / idealDCG);
        }
        Iterator<Double> real_dcgIT = real_dcg_values.iterator(), ideal_dcgIt = ideal_dcg_values.iterator();
        while (real_dcgIT.hasNext() && ideal_dcgIt.hasNext()) {
            double a = real_dcgIT.next() / ideal_dcgIt.next();
            avg_ndcg += a;
        }

        return Math.round(avg_ndcg / mdTest.size() * 1000.0) / 1000.0;
    }

    private static double log2(double number) {
        return Math.log10(number) / Math.log10(2);
    }

    public double getGlobalPrecisionTP() {
        return globalPrecisionTP;
    }

    public double getGlobalPrecisionRetrieved() {
        return globalPrecisionRetrieved;
    }

    public double getGlobalRecallTP() {
        return globalRecallTP;
    }

    public double getGlobalRecallFN() {
        return globalRecallFN;
    }

}

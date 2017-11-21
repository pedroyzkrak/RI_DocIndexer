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

    public static Map<Integer, List<MetricsData>> parseResults(String fileName) {
        Map<Integer, List<MetricsData>> queryInfo = new HashMap<>();
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

        return (double) Math.round(precision * 100000) / 100000;
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

        return (double) Math.round(recall * 100000) / 100000;
    }

    public static double calculateF_Measure(double precision, double recall) {
        return (double) Math.round(((2 * recall * precision) / (recall + precision)) * 100000) / 100000;
    }

    public static double calculateAveragePrecision(List<MetricsData> mdBase, List<MetricsData> mdTest, int cap) {
        double avrg_prec = 0;
        int tp = 0;
        int counter = 0;
        for (MetricsData d : mdTest) {
            counter++;
            if (mdBase.contains(d)) {
                tp++;
                avrg_prec += (double) tp / counter;
            }
        }
        return (double) Math.round((avrg_prec / tp) * 100000) / 100000;
    }

    public static double calculateMRR(List<MetricsData> mdBase, List<MetricsData> mdTest) {
        int index;
        double mmr = 0.0;
        index = mdTest.indexOf(mdBase.get(0));
        if (index > 0) {
            mmr = (double) 1 / (index + 1);
        }
        return (double) Math.round(mmr * 100000) / 100000;
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

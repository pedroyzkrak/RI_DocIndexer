/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package metrics;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import support.MetricsData;
import support.SearchData;

public class MetricsCalculation {

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
            e.printStackTrace();
        }
        queryInfo.entrySet().stream().forEach((mdList) -> {
            Collections.sort(mdList.getValue());
        });
        return queryInfo;
    }

    public static double calculatePrecision(List<MetricsData> mdBase, List<MetricsData> mdTest, int cap) { //sem cap tem q ser <1
        double precision = 0;
        int tp = 0;
        if (cap >= 1) {
            mdBase = mdBase.subList(0, cap);
        }
        for (MetricsData d : mdBase) {
            if (mdTest.contains(d)) {
                tp++;
            }

        }
        precision = tp / mdTest.size();

        return (double) Math.round(precision * 100) / 100;
    }

    public static double calculateRecall(List<MetricsData> mdBase, List<MetricsData> mdTest) {
        double recall = 0;
        int fn = 0;
        int tp = 0;
        for (MetricsData d : mdBase) {
            if (!mdTest.contains(d)) {
                fn++;
            } else {
                tp++;
            }

        }
        recall = tp / (tp + fn);

        return (double) Math.round(recall * 100) / 100;
    }

    public static double calculateF_Measure(double precision, double recall) {
        return ((2 * recall * precision) / (recall + precision));
    }

    public static double calculateAverage_Precision(List<MetricsData> mdBase, List<MetricsData> mdTest) {
        double avrg_prec = 0;
        int tp = 0;
        int counter = 0;
        for (MetricsData d : mdTest) {
            counter++;
            if (mdBase.contains(d)) {
                tp++;
                avrg_prec += tp / counter;
            }
        }
        return (avrg_prec / tp);
    }

    public static double calculateMRR(List<MetricsData> mdBase, List<MetricsData> mdTest) {
        int index = 0;
        int mmr = 0;
        index = mdTest.indexOf(mdBase.get(0));
        if (index > 0) {
            mmr += (1 / (index + 1));
        }
        return mmr;
    }

}

package save;

import interfaces.Indexer;
import indexer.SimpleIndexer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import indexer.WeightIndexer;
import support.Posting;
import support.SearchData;

/**
 * @author Francisco Lopes 76406
 * @author Pedro Gusmão 77867
 */
public class SaveToFile {

    /**
     * Saves the resulting index to a file
     *
     * @param indexer the resulting index to be saved
     */
    public static void saveIndex(Indexer indexer, String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {
            for (Map.Entry<String, LinkedList<Posting>> entry : indexer.entrySet()) {
                writer.write(entry.getKey());
                for (Posting doc : entry.getValue()) {
                    if (indexer instanceof SimpleIndexer)
                        writer.write("," + doc.getDocId() + ":" + doc.getTermFreq());
                    else if (indexer instanceof WeightIndexer)
                        writer.write("," + doc.getDocId() + ":" + doc.getWeight());
                    else
                        System.err.println("Instance error");
                }
                writer.write("\n");
            }
        } catch (IOException ex) {
            Logger.getLogger(SaveToFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    /**
     * Saves the query results and document scores to a file
     *
     * @param results  list SearchData objects with information about the query
     * @param fileName name of the file
     */
    public static void saveResults(List<SearchData> results, String fileName) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName, true))) {
            if (results.size() == 0 || results.get(0).getQuery().getId() == 1) {
                BufferedWriter clear = new BufferedWriter(new FileWriter(fileName));
                clear.write("");
                // bw.append("query_id doc_id doc_score\n");
            }
            String formatStr = "%s %s %s%n";
            for (SearchData data : results) {
                bw.append(String.format(formatStr, data.getQuery().getId(), data.getDocId(), data.getScore()));
            }
        } catch (IOException ex) {
            Logger.getLogger(SaveToFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Saves evaluation metrics to a file
     *
     * @param precision    precision results
     * @param precisionCap precision for the first 10 results
     * @param recall       recall results
     * @param fMeasure     f-measure results
     * @param map          mean average precision results
     * @param mrr          mean reciprocal rank results
     * @param ndcg         normalized discounted cumulative gain results
     * @param queryId      query ID
     * @param fileName     name of the output file
     */
    public static void saveMetrics(double precision, double precisionCap, double recall, double fMeasure, double map, double mrr, double ndcg, int queryId, String fileName) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName, true))) {
            String formatStr = "  %3s |   %-7s | %-7s | %-7s |   %-7s  |     %-7s    |      %-7s    | %-7s %n";

            bw.append(String.format(formatStr, queryId, precision, precisionCap, recall, fMeasure, map, mrr, ndcg));

        } catch (IOException ex) {
            Logger.getLogger(SaveToFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Saves efficiency metrics to a file
     *
     * @param text      text string to write
     * @param fileName  name of the output file
     * @param overwrite true will overwrite the file
     */
    public static void saveMetrics(String text, String fileName, boolean overwrite) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName, true))) {
            if (overwrite) {
                BufferedWriter clear = new BufferedWriter(new FileWriter(fileName));
                clear.write("");
            }

            bw.append(text);

        } catch (IOException ex) {
            Logger.getLogger(SaveToFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void saveMetrics(String queryTimes, String fileName) {
        saveMetrics(queryTimes, fileName, true);
    }

}

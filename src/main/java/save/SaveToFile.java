/**
 * Class built to save the index/results to a file
 */
package save;

import indexer.SimpleIndexer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    public static void saveIndex(SimpleIndexer indexer, String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {
            for (Map.Entry<String, LinkedList<Posting>> entry : indexer.entrySet()) {
                writer.write(entry.getKey());
                for (Posting doc : entry.getValue()) {
                    writer.write(", " + doc.getDocId() + ":" + doc.getDocFreq());
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
            if (results.get(0).getQuery().getId() == 1) {
                BufferedWriter clear = new BufferedWriter(new FileWriter(fileName, true));
                clear.write("");
                bw.append("query_id \t doc_id \t doc_score\n");
            }
            String formatStr = "%-8s \t %-6s \t %-9s%n";
            for (SearchData data : results) {
                bw.append(String.format(formatStr, data.getQuery().getId(), data.getDocId(), data.getScore()));
            }
        } catch (IOException ex) {
            Logger.getLogger(SaveToFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

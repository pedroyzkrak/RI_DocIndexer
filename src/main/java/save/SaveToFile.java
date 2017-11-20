package save;

import indexer.Indexer;
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
                bw.append("query_id doc_id doc_score\n");
            }
            String formatStr = "%s %s %s%n";
            for (SearchData data : results) {
                bw.append(String.format(formatStr, data.getQuery().getId(), data.getDocId(), data.getScore()));
            }
        } catch (IOException ex) {
            Logger.getLogger(SaveToFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

/**
 * 
 * Class built to save the index to a file
 * 
 */
package save;

import indexer.SimpleIndexer;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import posting.Posting;

/**
 *
 * @author Francisco Lopes 76406 
 * @author Pedro Gusmão 77867
 */
public class SaveToFile {
    
    /**
     * Saves the resulting index to a file
     * @param indexer the resulting to be saved
     */
    public static void save(SimpleIndexer indexer, String fileName) {
        try {
            try (FileWriter writer = new FileWriter(fileName)) {
                for (Map.Entry<String, LinkedList<Posting>> entry : indexer.entrySet()) {
                    writer.write(entry.getKey());
                    for (Posting doc : entry.getValue()) {
                        writer.write(", "+doc.getDocId()+":"+doc.getDocFreq());
                    }
                    writer.write("\n");
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(SaveToFile.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
    
    /*
    *
    * Saves indexed content to a file
    *
    */

    public static void save(SimpleIndexer indexer) {
        try {
            try (FileWriter writer = new FileWriter("SaveIndex.txt")) {
                writer.write("*FORMAT*\nTerm, DocId:DocFreq\n\n");
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

/**
 * Class built for the index reader
 */
package indexer;


import posting.Posting;
import save.SaveToFile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

/**
 * @author Francisco Lopes 76406
 * @author Pedro Gusmão 77867
 * <p>
 * A class that reads an index from disk
 */
public class IndexReader {

    public static SimpleIndexer readIndex(String indexFile) {
        SimpleIndexer indexer = new SimpleIndexer();
        LinkedList<Posting> postings = new LinkedList<>();

        try (BufferedReader in = new BufferedReader(new FileReader(indexFile))) {
            String line, term = "";
            int docID, docFreq;
            boolean isTerm;
            while ((line = in.readLine()) != null) {
                //index = "";
                isTerm = true;
                for (String l : line.split(", ")) {
                    if (isTerm) {
                        term = l;
                        isTerm = false;
                    } else {
                        docID = Integer.parseInt(l.split(":")[0]);
                        docFreq = Integer.parseInt(l.split(":")[1]);

                        postings.add(new Posting(docID, docFreq));
                    }
                }
                indexer.indexFromFile(term, postings);
                SaveToFile.save(indexer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return indexer;
    }


}

package reader;


import indexer.SimpleIndexer;
import support.Posting;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

/**
 * @author Francisco Lopes 76406
 * @author Pedro Gusmão 77867
 * <p>
 * A class that reads an index saved on disk
 */
public class IndexReader {


    /**
     * Loads an index from a file
     *
     * @param indexFile name of the file to load the index from
     * @return the loaded index
     */
    public static SimpleIndexer loadIndex(String indexFile) {
        SimpleIndexer indexer = new SimpleIndexer();
        LinkedList<Posting> postings = new LinkedList<>();

        try (BufferedReader in = new BufferedReader(new FileReader(indexFile))) {
            String line, term;
            int docID;
            double docFreq;
            boolean isTerm;
            while ((line = in.readLine()) != null) {
                term = "";
                isTerm = true;
                postings.clear();
                for (String l : line.split(",")) {
                    if (isTerm) {
                        term = l;
                        isTerm = false;
                    } else {
                        docID = Integer.parseInt(l.split(":")[0]);
                        docFreq = Double.parseDouble(l.split(":")[1]);

                        postings.add(new Posting(docID, docFreq));
                    }
                }
                indexer.indexFromFile(term, postings);
            }
        } catch (FileNotFoundException e) {
            System.err.println("File " + indexFile + " not found. Index could not be loaded.");
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Index Loaded.\n");

        return indexer;
    }


}

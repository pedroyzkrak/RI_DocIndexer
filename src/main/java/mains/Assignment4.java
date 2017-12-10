package mains;

import corpusReader.CorpusReader;
import indexer.SimpleIndexer;
import indexer.WeightIndexer;
import interfaces.Tokenizer;
import searcher.RankedSearcher;
import searcher.RocchioSearcher;
import tokenizer.SimpleTokenizer;

/**
 * Class that runs Assignment 4
 *
 * @author Francisco Lopes 76406
 * @author Pedro Gusmão 77867
 */
public class Assignment4 {
    public static void main() {

        // Variable initialization
        Tokenizer tokenizer = new SimpleTokenizer();
        WeightIndexer wi = new WeightIndexer("rocchio");

        long dirStart, dirEnd, indexStart, indexEnd;

        // Read Directory Section
        dirStart = System.currentTimeMillis();

        // build WeightIndexer
        CorpusReader.readAndProcessDir("cranfield", tokenizer, wi);

        dirEnd = System.currentTimeMillis();
        System.out.println("Read Dir time: " + (dirEnd - dirStart) / 1000.0 + " s\n");

        // Read queries with weighted terms
        System.out.println("Rocchio Metrics");
        indexStart = System.currentTimeMillis();

        RocchioSearcher.readQueryFromFile("cranfield.queries.txt", "SaveResultsRocchio.txt", "explicit", wi);

        indexEnd = System.currentTimeMillis();
        System.out.println("Querying Time: " + (indexEnd - indexStart) / 1000.0 + " s\n");
    }
}

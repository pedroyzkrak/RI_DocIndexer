package mains;

import reader.CorpusReader;
import indexer.SimpleIndexer;
import interfaces.Tokenizer;
import save.SaveToFile;
import support.Posting;
import tokenizer.SimpleTokenizer;

/**
 * Class that runs Assignment 1
 *
 * @author Francisco Lopes 76406
 * @author Pedro Gusmão 77867
 */
public class Assignment1 {
    public static void main() {
        Tokenizer tokenizer = new SimpleTokenizer();
        SimpleIndexer indexer = new SimpleIndexer();

        long tStart = System.currentTimeMillis();
        CorpusReader.readAndProcessDir("cranfield", tokenizer, indexer);

        long tEnd = System.currentTimeMillis();
        long tDelta = tEnd - tStart;
        double elapsedSeconds = tDelta / 1000.0;
        System.out.println("Elapsed Time: " + elapsedSeconds);

        SaveToFile.saveIndex(indexer, "SaveIndex.txt");

        System.out.println();


        // Question 4
        System.out.println("Top 10 (alphabetically) terms that appear in only one document");
        for (String term : indexer.getSingleTerms(10)) {
            System.out.println("Term: " + term);
        }
        System.out.println();

        System.out.println("Top 10 Highest Frequency terms");
        for (Posting freq : indexer.getHighestFrequency(10))
            System.out.println("Term: " + freq.getTerm() + " Frequency: " + (int) freq.getTermFreq());
    }
}

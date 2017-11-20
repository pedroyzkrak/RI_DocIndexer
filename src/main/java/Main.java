
import tokenizer.SimpleTokenizer;
import indexer.IndexReader;
import indexer.Indexer;
import indexer.SimpleIndexer;
import indexer.WeightIndexer;
import save.SaveToFile;
import corpusReader.CorpusReader;
import searcher.RankedSearcher;
import searcher.SimpleSearcher;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Francisco Lopes 76406
 * @author Pedro Gusmão 77867
 */
public class Main {

    public static void main(String[] args) {

        SimpleTokenizer tokenizer = new SimpleTokenizer();
        // Indexer indexer = new SimpleIndexer();
        Indexer indexer = new WeightIndexer();

        long dirStart = System.currentTimeMillis();

        CorpusReader.readAndProcessDir("cranfield", tokenizer, indexer);

        
        long dirEnd = System.currentTimeMillis();
        long dirDelta = dirEnd - dirStart;
        double dirElapsedSeconds = dirDelta / 1000.0;
        System.out.println("Read Dir time: " + dirElapsedSeconds);

        //SaveToFile.saveIndex(indexer, "SaveIndex.txt");
        SaveToFile.saveIndex(indexer, "SaveWeightIndex.txt");

        // DEBUG

        long indexStart = System.currentTimeMillis();

        RankedSearcher.readQueryFromFile("cranfield.queries.txt", "SaveResultsRanked.txt", indexer);
        long indexEnd = System.currentTimeMillis();
        long indexDelta = indexEnd - indexStart;
        double indexElapsedSeconds = indexDelta / 1000.0;
        System.out.println("Querying Time: " + indexElapsedSeconds);



        /*
        // Question 4
        for (String term : indexer.getSingleTerms(10)) {
            System.out.println("Term: " + term);
        }
        
        for (Posting freq : indexer.getHighestFrequency(10))
            System.out.println("Term: " + freq.getTerm() + " DocFreq: " + freq.getTermFreq());
        */

        //SaveToFile.saveIndex(IndexReader.loadIndex("SaveIndex.txt"), "newIndex.txt");

        /*
        long tStart = System.currentTimeMillis();

        SimpleIndexer si = IndexReader.loadIndex("SaveIndex.txt");

        SimpleSearcher.readQueryFromFile("cranfield.queries.txt", "SaveResultsWords.txt", "words", si);
        long tEnd = System.currentTimeMillis();
        long tDelta = tEnd - tStart;
        double elapsedSeconds = tDelta / 1000.0;
        System.out.println("Elapsed Time: "+elapsedSeconds);

        tStart = System.currentTimeMillis();
        SimpleSearcher.readQueryFromFile("cranfield.queries.txt", "SaveResultsFrequency.txt", "frequency", si);
        tEnd = System.currentTimeMillis();
        tDelta = tEnd - tStart;
        elapsedSeconds = tDelta / 1000.0;
        System.out.println("Elapsed Time: "+elapsedSeconds);

        */

    }
}

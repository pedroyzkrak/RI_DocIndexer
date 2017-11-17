
import Tokenizer.SimpleTokenizer;
import indexer.IndexReader;
import indexer.Indexer;
import indexer.SimpleIndexer;
import indexer.WeightIndexer;
import save.SaveToFile;
import corpusReader.CorpusReader;
import searcher.SimpleSearcher;

import java.io.IOException;

/**
 * @author Francisco Lopes 76406
 * @author Pedro Gusmão 77867
 */
public class Main {

    public static void main(String[] args) {

        SimpleTokenizer tokenizer = new SimpleTokenizer();
        // Indexer indexer = new SimpleIndexer();
        Indexer indexer = null;
        try {
            indexer = new WeightIndexer();
        } catch (IOException e) {
            e.printStackTrace();
        }

        long tStart = System.currentTimeMillis();

        CorpusReader.readAndProcessDir("cranfield", tokenizer, indexer);

        
        long tEnd = System.currentTimeMillis();
        long tDelta = tEnd - tStart;
        double elapsedSeconds = tDelta / 1000.0;
        System.out.println("Elapsed Time: "+elapsedSeconds);

        assert (indexer) != null;
        ((WeightIndexer)indexer).weightIndex();

        //SaveToFile.saveIndex(indexer, "SaveIndex.txt");
        SaveToFile.saveIndex(indexer, "SaveWeightIndex.txt");

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


import Tokenizer.SimpleTokenizer;
import indexer.IndexReader;
import indexer.SimpleIndexer;
import save.SaveToFile;
import corpusReader.CorpusReader;
import searcher.SimpleSearcher;

/**
 * @author Francisco Lopes 76406
 * @author Pedro Gusmão 77867
 */
public class Main {

    public static void main(String[] args) {
        /*
        SimpleTokenizer tokenizer = new SimpleTokenizer();
        SimpleIndexer indexer = new SimpleIndexer();
        
        long tStart = System.currentTimeMillis();

        CorpusReader.readAndProcessDir("cranfield", tokenizer, indexer);
        
        long tEnd = System.currentTimeMillis();
        long tDelta = tEnd - tStart;
        double elapsedSeconds = tDelta / 1000.0;
        System.out.println("Elapsed Time: "+elapsedSeconds);

        SaveToFile.saveIndex(indexer, "SaveIndex.txt");
        
        /*
        // Question 4
        for (String term : indexer.getSingleTerms(10)) {
            System.out.println("Term: " + term);
        }
        
        for (Posting freq : indexer.getHighestFrequency(10))
            System.out.println("Term: " + freq.getTerm() + " DocFreq: " + freq.getDocFreq());
        */

        //SaveToFile.saveIndex(IndexReader.loadIndex("SaveIndex.txt"), "newIndex.txt");


        long tStart = System.currentTimeMillis();

        SimpleSearcher.readQueryFromFile("cranfield.queries.txt", "SaveResultsWords.txt", "words");
        long tEnd = System.currentTimeMillis();
        long tDelta = tEnd - tStart;
        double elapsedSeconds = tDelta / 1000.0;
        System.out.println("Elapsed Time: "+elapsedSeconds);

        tStart = System.currentTimeMillis();
        SimpleSearcher.readQueryFromFile("cranfield.queries.txt", "SaveResultsFrequency.txt", "frequency");
        tEnd = System.currentTimeMillis();
        tDelta = tEnd - tStart;
        elapsedSeconds = tDelta / 1000.0;
        System.out.println("Elapsed Time: "+elapsedSeconds);


    }
}

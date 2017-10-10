
import Tokenizer.SimpleTokenizer;
import indexer.SimpleIndexer;
import save.SaveToFile;
import corpusReader.CorpusReader;
//import posting.Posting;

/**
 *
 * @author Francisco Lopes 76406
 * @author Pedro Gusmão 77867
 */
public class Main {

    public static void main(String[] args) {
        SimpleTokenizer tokenizer = new SimpleTokenizer();
        SimpleIndexer indexer = new SimpleIndexer();
        
        long tStart = System.currentTimeMillis();

        CorpusReader.readAndProcessDir("cranfield", tokenizer, indexer);
        
        long tEnd = System.currentTimeMillis();
        long tDelta = tEnd - tStart;
        double elapsedSeconds = tDelta / 1000.0;
        System.out.println("Elapsed Time: "+elapsedSeconds);

        SaveToFile.save(indexer);
        
        /*
        // Question 4
        for (String term : indexer.getSingleTerms(10)) {
            System.out.println("Term: " + term);
        }
        
        for (Posting freq : indexer.getHighestFrequency(10))
            System.out.println("Term: " + freq.getTerm() + " DocFreq: " + freq.getDocFreq());*/
    }
}

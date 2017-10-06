
import Tokenizer.SimpleTokenizer;
import indexer.SimpleIndexer;
import java.util.LinkedList;
import java.util.Map;
import posting.Posting;
import save.SaveToFile;
import corpusReader.CorpusReader;

/**
 *
 * @author Francisco Lopes 76406
 * @author Pedro Gusmão 77867
 */
public class MainTest {

    public static void main(String[] args) {
        SimpleTokenizer tokenizer = new SimpleTokenizer();
        SimpleIndexer indexer = new SimpleIndexer();

        Runtime runtime = Runtime.getRuntime();

        double usedMemoryBefore = (double) (runtime.totalMemory() - runtime.freeMemory()) / (double) (1024 * 1024);
        System.out.println("Used Memory before: " + usedMemoryBefore + " MB");
        long tStart = System.currentTimeMillis();

        CorpusReader.readAndProcessDir("cranfield", tokenizer, indexer);

        long tEnd = System.currentTimeMillis();
        long tDelta = tEnd - tStart;
        double elapsedSeconds = tDelta / 1000.0;
        double usedMemoryAfter = (double) (runtime.totalMemory() - runtime.freeMemory()) / (double) (1024 * 1024);
        System.out.println("Used Memory after: " + usedMemoryAfter + " MB");
        System.out.println("Memory increased: " + (usedMemoryAfter - usedMemoryBefore) + " MB");
        System.out.println("Elapsed Time: "+elapsedSeconds);

        SaveToFile.save(indexer);

        /*
        //CorpusReader.readDir("cranfield");
        SimpleTokenizer tokenizer = new SimpleTokenizer();
        SimpleIndexer indexer = new SimpleIndexer();

        try {
            tokenizer.tokenize("Hey gd 31are you#$ 31?", "[a-z]{3,}", false, false); //regex

        } catch (ParserException e) {
            System.out.println(e.getMessage());
        }

        for (SimpleTokenizer.Token tok : tokenizer.getTokens()) {
            System.out.println("" + tok.getSequence());
        }
        
        tokenizer.clear();
        
        //*TESTS*
        
        
        //Simple Tokenizer
        System.out.println("\nSimple Tokenizer");
        System.out.println("-------------------------");
        tokenizer.tokenize("Taste the feeling, so damn frosty, people like, Damn! That's a cool as honkey", "[a-z]{3,}", false, false);
        indexer.index(tokenizer.getTokens(), 1);
        tokenizer.clear();
        tokenizer.tokenize("Follow me to the end of the world", "[a-z]{3,}", false, false);
        indexer.index(tokenizer.getTokens(), 2);
        tokenizer.clear();
        tokenizer.tokenize("I'm going on an adventure to see the world to describe my feelings", "[a-z]{3,}", false, false);
        indexer.index(tokenizer.getTokens(), 3);
        tokenizer.clear();
        
        for (Map.Entry<String, LinkedList<Posting>> entry : indexer.entrySet()) {
            System.out.println("Token: " + entry.getKey());
            for (Posting doc : entry.getValue()) {
                System.out.println("\tDoc ID: " + doc.getDocId() + " Doc Freq: " + doc.getDocFreq());
            }
        }
        
        indexer.clear();
        
        
        // Stemmer
        System.out.println("\nStemmer");
        System.out.println("-------------------------");
        tokenizer.tokenize("Taste the feeling, so damn frosty, people like, Damn! That's a cool as honkey", "[a-z]{3,}", true, false);
        indexer.index(tokenizer.getTokens(), 1);
        tokenizer.clear();
        tokenizer.tokenize("Follow me to the end of the world", "[a-z]{3,}", true, false);
        indexer.index(tokenizer.getTokens(), 2);
        tokenizer.clear();
        tokenizer.tokenize("I'm going on an adventure to see the world to describes my feeling", "[a-z]{3,}", true, false);
        indexer.index(tokenizer.getTokens(), 3);
        tokenizer.clear();
        
        for (Map.Entry<String, LinkedList<Posting>> entry : indexer.entrySet()) {
            System.out.println("Token: " + entry.getKey());
            for (Posting doc : entry.getValue()) {
                System.out.println("\tDoc ID: " + doc.getDocId() + " Doc Freq: " + doc.getDocFreq());
            }
        }
        
        indexer.clear();
        
        
        //Stop word filter
        System.out.println("\nStopWord");
        System.out.println("-------------------------");
        tokenizer.tokenize("Taste the feeling, so damn frosty, people like, Damn! That's a cool as honkey", "[a-z]{3,}", false, true);
        indexer.index(tokenizer.getTokens(), 1);
        tokenizer.clear();
        tokenizer.tokenize("Follow me to the end of the world", "[a-z]{3,}", false, true);
        indexer.index(tokenizer.getTokens(), 2);
        tokenizer.clear();
        tokenizer.tokenize("I'm going on an adventure to see the world to describe my feelings", "[a-z]{3,}", false, true);
        indexer.index(tokenizer.getTokens(), 3);
        tokenizer.clear();
        
        for (Map.Entry<String, LinkedList<Posting>> entry : indexer.entrySet()) {
            System.out.println("Token: " + entry.getKey());
            for (Posting doc : entry.getValue()) {
                System.out.println("\tDoc ID: " + doc.getDocId() + " Doc Freq: " + doc.getDocFreq());
            }
        }
        
        indexer.clear();
        
        
        //Stopword and Stemmer
        System.out.println("\nBoth");
        System.out.println("-------------------------");
        tokenizer.tokenize("Taste the feeling, so damn frosty, people like, Damn! That's a cool as honkey", "[a-z]{3,}", true, true);
        indexer.index(tokenizer.getTokens(), 1);
        tokenizer.clear();
        tokenizer.tokenize("Follow me to the end of the world", "[a-z]{3,}", true, true);
        indexer.index(tokenizer.getTokens(), 2);
        tokenizer.clear();
        tokenizer.tokenize("I'm going on an adventure to see the world to describe my feelings", "[a-z]{3,}", true, true);
        indexer.index(tokenizer.getTokens(), 3);
        tokenizer.clear();


        for (Map.Entry<String, LinkedList<Posting>> entry : indexer.entrySet()) {
            System.out.println("Token: " + entry.getKey());
            for (Posting doc : entry.getValue()) {
                System.out.println("\tDoc ID: " + doc.getDocId() + " Doc Freq: " + doc.getDocFreq());
            }
        }*/
    }
}

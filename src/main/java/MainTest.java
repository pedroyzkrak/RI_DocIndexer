
import Exception.ParserException;
import Tokenizer.SimpleTokenizer;
import indexer.SimpleIndexer;
import java.util.LinkedList;
import java.util.Map;
import posting.Posting;
//import corpusReader.CorpusReader;

public class MainTest {

    public static void main(String[] args) {
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
        
        
        // Stemmer
        System.out.println("\nStemmer");
        System.out.println("-------------------------");
        tokenizer.tokenize("Taste the feeling, so damn frosty, people like, Damn! That's a cool as honkey", "[a-z]{3,}", true, false);
        indexer.index(tokenizer.getTokens(), 1);
        tokenizer.clear();
        tokenizer.tokenize("Follow me to the end of the world", "[a-z]{3,}", true, false);
        indexer.index(tokenizer.getTokens(), 2);
        tokenizer.clear();
        tokenizer.tokenize("I'm going on an adventure to see the world to describe my feelings", "[a-z]{3,}", true, false);
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
        }
    }
}

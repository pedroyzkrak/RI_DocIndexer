package thesaurus;

import interfaces.Tokenizer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tokenizer.SimpleTokenizer;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Thesaurus {
    private Word2Vec vec;
    private HashMap<String, ArrayList<String>> neighbourCache;

    private static Logger log = LoggerFactory.getLogger(Thesaurus.class);

    /**
     * Contructor that initializes and builds a cache of a thesaurus of word embedding vectors
     *
     * @param fileName    file name of the training model
     * @param queriesFile file containing the queries to process
     * @param n           number of similar words to generate from a query word
     * @throws FileNotFoundException if training file doesn't exists/is not found
     */
    public Thesaurus(String fileName, String queriesFile, int n) throws FileNotFoundException {
        this.vec = generateWord2Vec(fileName);
        neighbourCache = loadNeighbourCache(queriesFile, n);
    }

    /**
     * Generates a thesaurus of word embedding vectors
     *
     * @param fileName file name of the training model
     * @return a thesaurus of word embedding vectors
     * @throws FileNotFoundException if file doesn't exists/is not found
     */
    private Word2Vec generateWord2Vec(String fileName) throws FileNotFoundException {
        /*
        * Original work from DL4J - Deeplearning4J
        * GitHub: https://github.com/deeplearning4j/dl4j-examples
        */

        File file = new File(fileName);

        if (!file.exists()) {
            System.err.println("File " + file + " doesn't exist.");
            System.exit(1);
        }

        // Gets Path to text file
        String filePath = file.getAbsolutePath();

        log.info("Load & Vectorize Sentences....");

        // Strip white space before and after for each line
        SentenceIterator iter = new BasicLineIterator(filePath);

        // Split on white spaces in the line to get words
        TokenizerFactory t = new DefaultTokenizerFactory();

        /*
            CommonPreprocessor will apply the following regex to each token: [\d\.:,"'\(\)\[\]|/?!;]+
            So, effectively all numbers, punctuation symbols and some special symbols are stripped off.
            Additionally it forces lower case for all tokens.
         */

        t.setTokenPreProcessor(new CommonPreprocessor());

        log.info("Building model....");
        Word2Vec vec = new Word2Vec.Builder()
                .minWordFrequency(5)
                .iterations(1)
                .layerSize(100)
                .seed(42)
                .windowSize(5)
                .iterate(iter)
                .tokenizerFactory(t)
                .build();

        log.info("Fitting Word2Vec model....");
        vec.fit();

        return vec;
    }


    /**
     * Expands the original query with similar words
     *
     * @param query the string of words from the query
     * @return an expanded query containing similar words from the original query
     */
    public String getExpandedQuery(String query) {
        StringBuilder expandedQuery = new StringBuilder(query);

        Tokenizer tkn = new SimpleTokenizer();
        tkn.tokenize(query, "[a-zA-Z]{3,}", false, true);

        for (SimpleTokenizer.Token token : tkn.getTokens()) {
            String word = token.getSequence();
            if (neighbourCache.containsKey(word)) {
                for (String similarWord : neighbourCache.get(word))
                    expandedQuery.append(String.format(" %s", similarWord));
            }
        }

        return expandedQuery.toString();
    }

    /**
     * Loads a cache for every similar word of a query word for
     * faster querying processing times
     *
     * @param fileName file containing the queries to process
     * @return a cache for every similar word of a query word
     */
    private HashMap<String, ArrayList<String>> loadNeighbourCache(String fileName, int n) {
        System.out.println("Loading Similar word Cache...");
        long start = System.currentTimeMillis();
        Tokenizer tkn = new SimpleTokenizer();
        HashMap<String, ArrayList<String>> cache = new HashMap<>();
        String line;
        try (BufferedReader in = new BufferedReader(new FileReader(fileName))) {
            while ((line = in.readLine()) != null) {
                tkn.tokenize(line, "[a-zA-Z]{3,}", false, true);
                for (SimpleTokenizer.Token token : tkn.getTokens()) {
                    String word = token.getSequence();
                    if (!cache.containsKey(word)) {
                        cache.put(word, new ArrayList<>());
                        for (String similarWord : vec.wordsNearest(word, n)) {
                            cache.get(word).add(similarWord);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Similar Word Cache Loaded.");
        System.out.println("Elapsed time: " + (System.currentTimeMillis() - start) / 1000.0 + " s \n");

        return cache;
    }

}

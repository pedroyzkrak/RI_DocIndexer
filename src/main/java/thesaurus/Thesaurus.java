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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;

public class Thesaurus {
    private Word2Vec vec;
    private static Logger log = LoggerFactory.getLogger(Thesaurus.class);

    public Thesaurus(String fileName) throws FileNotFoundException {
        this.vec = generateWord2Vec(fileName);
    }

    /**
     * Generates a thesaurus of word embedding vectors
     *
     * @param fileName name of the file to train the model
     * @return a thesaurus of word embedding vectors
     * @throws FileNotFoundException if file doens't exists/is not found
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
     * @param n     number of similar words to obtain from each query word
     * @return an expanded query containing similar words from the original query
     */
    public String getSimilarWords(String query, int n) {
        StringBuilder expandedQuery = new StringBuilder(query);
        Tokenizer tkn = new SimpleTokenizer();
        tkn.tokenize(query, "[a-zA-Z]{3,}", false, true);

        for (SimpleTokenizer.Token token : tkn.getTokens()) {
            String word = token.getSequence();
            for (String similarWord : vec.wordsNearest(word, n)) {
                expandedQuery.append(String.format(" %s", similarWord));
            }
        }


        return expandedQuery.toString();
    }
}

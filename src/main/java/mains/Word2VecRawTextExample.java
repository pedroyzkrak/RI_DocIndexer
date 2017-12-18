package mains;

import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collection;

public class Word2VecRawTextExample {

    private static Logger log = LoggerFactory.getLogger(Word2VecRawTextExample.class);

    public static void main() throws Exception {

        File file = new File("cranfield_sentences.txt");

        if (!file.exists()) {
            System.err.println("File " + file + " doesn't exist.");
            System.exit(1);
        }

        // Gets Path to Text file
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

        log.info("Writing word vectors to text file....");

        // Prints out the closest 10 words to "day". An example on what to do with these Word Vectors.
        log.info("Closest Words:");
        long s = System.currentTimeMillis();
        Collection<String> lst = vec.wordsNearest("jet", 10);
        System.out.println("Elapsed Time : " + (System.currentTimeMillis() - s) / 1000.0);
        System.out.println("10 closest Words: " + lst);
    }
}

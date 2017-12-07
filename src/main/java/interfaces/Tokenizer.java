package interfaces;

import tokenizer.SimpleTokenizer;

import java.util.LinkedList;

/**
 * @author Francisco Lopes 76406
 * @author Pedro Gusmão 77867
 * <p>
 * Tokenizer Interface
 */
public interface Tokenizer {

    void tokenize(String str, String regex, boolean stem, boolean stopword);
    LinkedList<SimpleTokenizer.Token> getTokens();
    void clear();
}

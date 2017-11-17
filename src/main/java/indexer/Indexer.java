package indexer;

import Tokenizer.SimpleTokenizer;
import support.Posting;

import java.util.HashMap;
import java.util.LinkedList;

public interface Indexer {
    void index(LinkedList<SimpleTokenizer.Token> tokenList, int docId);

    HashMap<String, LinkedList<Posting>> getIndexer();
    void clear();
}

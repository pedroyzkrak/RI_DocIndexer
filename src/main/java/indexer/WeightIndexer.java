package indexer;

import Tokenizer.SimpleTokenizer;
import support.Posting;

import java.util.HashMap;
import java.util.LinkedList;

public class WeightIndexer implements Indexer {

    @Override
    public void index(LinkedList<SimpleTokenizer.Token> tokenList, int docId) {

    }

    @Override
    public HashMap<String, LinkedList<Posting>> getIndexer() {
        return null;
    }

    @Override
    public void clear() {

    }
}

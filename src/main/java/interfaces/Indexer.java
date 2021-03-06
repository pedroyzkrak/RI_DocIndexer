package interfaces;

import support.RankedData;
import tokenizer.SimpleTokenizer;
import support.Posting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;


/**
 * @author Francisco Lopes 76406
 * @author Pedro Gusmão 77867
 * <p>
 * Indexer Interface
 */
public interface Indexer {
    void index(LinkedList<SimpleTokenizer.Token> tokenList, int docId);
    HashMap<String, LinkedList<Posting>> getIndexer();
    void clear();
    Iterable<Map.Entry<String, LinkedList<Posting>>> entrySet();

    HashMap<Integer,ArrayList<RankedData>> getDocumentCache();
}

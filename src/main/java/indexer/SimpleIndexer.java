package indexer;


import interfaces.Indexer;
import support.Posting;
import tokenizer.SimpleTokenizer.Token;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.LinkedList;
import java.util.ListIterator;


/**
 * @author Francisco Lopes 76406
 * @author Pedro Gusmão 77867
 * <p>
 * Class for the simple indexer that it's main purpose is to index multiple given tokens
 */

public class SimpleIndexer implements Indexer {

    private final HashMap<String, LinkedList<Posting>> indexer;

    /**
     * The constructor of the indexer that initializes the Map
     */
    public SimpleIndexer() {
        this.indexer = new HashMap<>();
    }

    /**
     * Indexes the tokens of a document to a hash map containing the terms and the corresponding document with the frequency of said term
     *
     * @param tokenList The linked list of tokens
     * @param docId     The ID of the document
     */
    @Override
    public void index(LinkedList<Token> tokenList, int docId) {

        a:
        for (Token tok : tokenList) {

            // If token is not yet indexed
            if (!indexer.containsKey(tok.getSequence())) {
                indexer.put(tok.getSequence(), new LinkedList<>());
                indexer.get(tok.getSequence()).add(new Posting(docId, 1));
            } else {

                // check if the list contains a document linked to this token already
                for (Posting posting : indexer.get(tok.getSequence())) {
                    if (posting.getDocId() == docId) {
                        posting.setTermFreq(posting.getTermFreq() + 1);
                        continue a;
                    }
                }
                // add a new entry to the indexer for a new document that uses the token
                indexer.get(tok.getSequence()).add(new Posting(docId, 1));
            }
        }
    }


    /**
     * Builds an index given a term and the corresponding document IDs and document frequencies (from a file)
     * In case the index is not organized, it will regroup and and update the indexes
     * e.g
     * line 1: today, 1:2,1:1
     * (...)
     * line 2: today, 1:1,123:4
     * Result index -> today, 1:4,123:4
     *
     * @param term     The given term
     * @param postings The list of postings (with document ID and document frequency)
     */
    public void indexFromFile(String term, LinkedList<Posting> postings) {

        // if term not yet indexed
        if (!indexer.containsKey(term)) {
            indexer.put(term, new LinkedList<>());
            for (Posting posting : postings) {
                if (indexer.get(term).contains(posting)) {
                    // if a posting is on the index, update frequency
                    int postingIdx = indexer.get(term).indexOf(posting);
                    indexer.get(term).get(postingIdx).setTermFreq(indexer.get(term).get(postingIdx).getTermFreq() + posting.getTermFreq());
                } else {
                    indexer.get(term).add(posting);
                }
            }
        } else {
            ListIterator<Posting> iter = indexer.get(term).listIterator();
            Posting indexPosting;

            while (iter.hasNext()) {
                indexPosting = iter.next();
                for (Posting newPosting : postings) {
                    // update DocFreq in case postings have the same DocID
                    if (indexPosting.getDocId() == newPosting.getDocId()) {
                        indexPosting.setTermFreq(indexPosting.getTermFreq() + newPosting.getTermFreq());
                    }
                    // checks if the new posting is already on the index
                    else if (Posting.containsDocID(indexer.get(term), newPosting)) {
                        continue;
                    }
                    // add to the index if posting is a new one
                    else {
                        iter.add(newPosting);
                    }
                }
            }
        }
    }

    /**
     * @return the entry set of the index
     */
    public Iterable<Map.Entry<String, LinkedList<Posting>>> entrySet() {
        return indexer.entrySet();
    }


    @Override
    public HashMap<String, LinkedList<Posting>> getIndexer() {
        return indexer;
    }

    /**
     * clears the index
     */
    @Override
    public void clear() {
        indexer.clear();
    }

    /**
     * @param n number of the first terms to show
     * @return sorted list of the first n terms that appear in only one document
     */
    public List<String> getSingleTerms(int n) {
        LinkedList<String> singleTerms = new LinkedList<>();
        if (indexer.isEmpty()) {
            System.err.println("Indexer is empty.");
            return null;
        } else if (indexer.size() < n) {
            System.err.println("Value higher than indexer size.");
        }
        indexer.entrySet().stream().filter((entry) -> (entry.getValue().size() == 1)).forEachOrdered((entry) -> singleTerms.add(entry.getKey()));

        Collections.sort(singleTerms);

        return singleTerms.subList(0, n);
    }


    /**
     * @param n number of terms with highest frequency
     * @return sorted list in decreasing order of terms with highest frequency
     */
    public List<Posting> getHighestFrequency(int n) {
        LinkedList<Posting> termFreq = new LinkedList<>();
        int freq;

        for (Map.Entry<String, LinkedList<Posting>> entry : indexer.entrySet()) {
            freq = 0;
            for (Posting post : entry.getValue()) {
                freq += post.getTermFreq();

            }
            termFreq.add(new Posting(entry.getKey(), freq));
        }

        Collections.sort(termFreq);

        return termFreq.subList(0, n);
    }
}


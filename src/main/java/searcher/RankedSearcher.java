package searcher;

import Tokenizer.SimpleTokenizer;
import indexer.Indexer;
import save.SaveToFile;
import support.Posting;
import support.Query;
import support.RankedData;
import support.SearchData;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static java.lang.Math.*;

/**
 * @author Francisco Lopes 76406
 * @author Pedro Gusmão 77867
 * <p>
 * A class that implements a ranked retrieval method
 */
public class RankedSearcher {

    private static int N = -1;


    public static void readQueryFromFile(String fileName, String outputFile, Indexer wi) {
        try (BufferedReader in = new BufferedReader(new FileReader(fileName))) {
            String line;
            int id = 0;
            Query query;
            while ((line = in.readLine()) != null) {
                id++;
                query = new Query(id, line);
                SaveToFile.saveResults(rankedRetrieval(query, wi), outputFile);
                System.out.println("Processed " + id);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static List<SearchData> rankedRetrieval(Query query, Indexer wi) {
        SimpleTokenizer tkn = new SimpleTokenizer();
        tkn.tokenize(query.getStr(), "[a-zA-Z]{3,}", true, true);

        List<SearchData> searchList = new ArrayList<>();
        List<RankedData> queryTerms = new ArrayList<>();

        Iterator<SimpleTokenizer.Token> wordsIt = tkn.getTokens().iterator();

        HashMap<String, LinkedList<Posting>> indexer = wi.getIndexer();
        HashMap<Integer, LinkedList<RankedData>> search = new HashMap<>();

        // create a data set for the terms in the query (list) and for the docIDs that contain such terms (map)
        while (wordsIt.hasNext()) {
            String word = wordsIt.next().getSequence();
            RankedData rd = new RankedData(word, 1);
            if (queryTerms.contains(rd)) {
                RankedData term = queryTerms.get(queryTerms.indexOf(rd));
                term.setWeight(term.getWeight() + 1);
            }
            else
                queryTerms.add(rd);

            if (indexer.containsKey(word)) {
                for (Posting posting : indexer.get(word)) {
                    if (search.containsKey(posting.getDocId()))
                        search.get(posting.getDocId()).add(new RankedData(word, posting.getWeight()));
                    else {
                        search.put(posting.getDocId(), new LinkedList<>());
                        search.get(posting.getDocId()).add(new RankedData(word, posting.getWeight()));
                    }
                }
            }
        }

        // Calculate weight for every query term
        for (RankedData qt : queryTerms) {
            int df = indexer.get(qt.getTerm()).getFirst().getDocFreq();
            qt.setWeight((( 1 + log10(qt.getWeight())) * log10( getN() / df )));
        }

        double queryLength = calculateLength(queryTerms);

        // Normalize every query term weight
        for (RankedData qt : queryTerms) {
            double weightNorm = qt.getWeight() / queryLength;
            qt.setScore(weightNorm);
        }

        // Normalize every document term weight
        for (Map.Entry<Integer, LinkedList<RankedData>> s : search.entrySet()) {
            // get doc length
            double length = calculateLength(s.getValue());

            // normalize each document term weight
            for (RankedData rd : s.getValue()) {
                rd.setScore(rd.getWeight() / sqrt(length));
            }
        }

        // Aggregate a result list for each document score
        for (Map.Entry<Integer, LinkedList<RankedData>> s : search.entrySet()) {
            double score = 0;
            for (RankedData rd : queryTerms) {
                if (s.getValue().contains(rd)) {
                    double docScore = s.getValue().get(s.getValue().indexOf(rd)).getScore();
                    score += rd.getWeight() * docScore;
                }
            }
            SearchData sd = new SearchData(query, s.getKey());
            sd.setScore( (double) round(score * 1000) / 1000);
            searchList.add(sd);
        }
        Collections.sort(searchList);

        return searchList;
    }

    private static int getN() {
        if (N == -1) {
            List<String> fileNames = new ArrayList<>();
            try {
                DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get("cranfield"));
                for (Path path : directoryStream) {
                    fileNames.add(path.toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            N = fileNames.size();
        }
        return N;
    }

    private static double calculateLength(List<RankedData> values) {
        double weight, length = 0;
        for (RankedData v : values) {
            length += pow(v.getWeight(), 2);
        }
        weight = sqrt(length);
        return weight;
    }

}

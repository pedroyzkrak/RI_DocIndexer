
import Tokenizer.SimpleTokenizer;
import indexer.SimpleIndexer;
import save.SaveToFile;
import corpusReader.CorpusReader;

/**
 *
 * @author Francisco Lopes 76406
 * @author Pedro Gusmão 77867
 */
public class Main {

    public static void main(String[] args) {
        SimpleTokenizer tokenizer = new SimpleTokenizer();
        SimpleIndexer indexer = new SimpleIndexer();

        Runtime runtime = Runtime.getRuntime();

        double usedMemoryBefore = (double) (runtime.totalMemory() - runtime.freeMemory()) / (double) (1024 * 1024);
        System.out.println("Used Memory before: " + usedMemoryBefore + " MB");
        long tStart = System.currentTimeMillis();

        CorpusReader.readAndProcessDir("cranfield", tokenizer, indexer);

        long tEnd = System.currentTimeMillis();
        long tDelta = tEnd - tStart;
        double elapsedSeconds = tDelta / 1000.0;
        double usedMemoryAfter = (double) (runtime.totalMemory() - runtime.freeMemory()) / (double) (1024 * 1024);
        System.out.println("Used Memory after: " + usedMemoryAfter + " MB");
        System.out.println("Memory increased: " + (usedMemoryAfter - usedMemoryBefore) + " MB");
        System.out.println("Elapsed Time: "+elapsedSeconds);

        SaveToFile.save(indexer);
    }
}

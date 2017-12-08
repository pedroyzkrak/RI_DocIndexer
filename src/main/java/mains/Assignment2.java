package mains;

/**
 * Class that runs Assignment 2
 * @author Francisco Lopes 76406
 * @author Pedro Gusmão 77867
 */
public class Assignment2 {
    public static void main() {
        //SaveToFile.saveIndex(IndexReader.loadIndex("SaveIndex.txt"), "newIndex.txt");

        /*
        long tStart = System.currentTimeMillis();

        SimpleIndexer si = IndexReader.loadIndex("SaveIndex.txt");

        SimpleSearcher.readQueryFromFile("cranfield.queries.txt", "SaveResultsWords.txt", "words", si);
        long tEnd = System.currentTimeMillis();
        long tDelta = tEnd - tStart;
        double elapsedSeconds = tDelta / 1000.0;
        System.out.println("Elapsed Time: "+elapsedSeconds);

        tStart = System.currentTimeMillis();
        SimpleSearcher.readQueryFromFile("cranfield.queries.txt", "SaveResultsFrequency.txt", "frequency", si);
        tEnd = System.currentTimeMillis();
        tDelta = tEnd - tStart;
        elapsedSeconds = tDelta / 1000.0;
        System.out.println("Elapsed Time: "+elapsedSeconds);

        */
    }
}

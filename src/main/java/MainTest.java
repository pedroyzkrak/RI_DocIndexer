import Exception.ParserException;
import Tokenizer.SimpleTokenizer;
//import corpusReader.CorpusReader;

public class MainTest {

    public static void main(String[] args) {
        //CorpusReader.readDir("cranfield");
        SimpleTokenizer tokenizer = new SimpleTokenizer();

        try {
            tokenizer.tokenize("Hey 31are you#$ 31?", "[a-zA-Z]{3,}");

            for (SimpleTokenizer.Token tok : tokenizer.getTokens()) {
                System.out.println("" + tok.sequence);
            }
        } catch (ParserException e) {
            System.out.println(e.getMessage());
        }

    }
}


import Exception.ParserException;
import Tokenizer.SimpleTokenizer;
//import corpusReader.CorpusReader;

public class MainTest {

    public static void main(String[] args) {
        //CorpusReader.readDir("cranfield");
        SimpleTokenizer tokenizer = new SimpleTokenizer();

        try {
            tokenizer.tokenize("Hey gd 31are you#$ 31?", "[a-z]{3,}"); //regex

        } catch (ParserException e) {
            System.out.println(e.getMessage());
        }

        for (SimpleTokenizer.Token tok : tokenizer.getTokens()) {
            System.out.println("" + tok.getSequence());
        }

    }
}

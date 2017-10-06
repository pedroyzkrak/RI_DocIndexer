/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Tokenizer;

import Exception.ParserException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.englishStemmer;

/**
 *
 * @author Francisco Lopes 76406
 * @author Pedro Gusmão 77867
 */
public class SimpleTokenizer {

    public class Token {

        private final String sequence;

        public String getSequence() {
            return sequence;
        }

        public Token(String sequence) {
            super();
            this.sequence = sequence;
        }

    }
    private LinkedList<Token> tokens;

    public SimpleTokenizer() {
        tokens = new LinkedList<>();
    }

    public void tokenize(String str, String regex, boolean stem, boolean stopword) {
        String s = str.toLowerCase().trim();
        List<String> stopwordArray = new ArrayList<>();
        SnowballStemmer stemmer = new englishStemmer();
        if (stopword) {
            File stopfile = new File("stop.txt");
            try (BufferedReader br = new BufferedReader(new FileReader(stopfile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    stopwordArray.add(line);
                }
            } catch (IOException ex) {
                Logger.getLogger(SimpleTokenizer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        while (!s.equals("")) {
            Matcher m = Pattern.compile(regex).matcher(s);
            if (m.find()) {
                String tok = m.group().trim();
                s = m.replaceFirst("").trim();

                if (stem) {
                    stemmer.setCurrent(tok);
                    stemmer.stem();
                    tok = stemmer.getCurrent();
                }
                if (!stopword) {
                    tokens.add(new Token(tok));
                } else {
                    if (!stopwordArray.contains(tok)) {
                        tokens.add(new Token(tok));
                    }
                }
            } else {
                break;
            }
        }
    }

    public LinkedList<Token> getTokens() {
        return tokens;
    }

    public void clear() {
        tokens.clear();
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Tokenizer;

import Exception.ParserException;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Pedro
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

    public void tokenize(String str, String regex) {
        String s = str.toLowerCase().trim();
        tokens.clear();
        while (!s.equals("")) {
            Matcher m = Pattern.compile(regex).matcher(s);
            if (m.find()) {
                String tok = m.group().trim();
                s = m.replaceFirst("").trim();
                tokens.add(new Token(tok));
            }
            else{
                throw new ParserException("Ignored characters in input: "+s);
            }
        }
    }

    public LinkedList<Token> getTokens() {
        return tokens;
    }
    
    public void clear()
    {
        tokens.clear();
    }
}

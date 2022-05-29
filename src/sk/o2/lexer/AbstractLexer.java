package sk.o2.lexer;

import java.util.ArrayList;
import java.util.regex.Pattern;

public abstract class AbstractLexer<TTokenType> {
    private String input;
    
    private final TTokenType badToken;
    private final TTokenType whitespaceToken;
    
    private final ArrayList<LexerToken<TTokenType>> tokens;
    private final ArrayList<LexerTokenDefinition<TTokenType>> tokenTypes;
    
    public AbstractLexer(String input, TTokenType badToken, TTokenType whitespaceToken) {
        this.input = input;
        this.badToken = badToken;
        this.whitespaceToken = whitespaceToken;
        
        tokens = new ArrayList<>();
        tokenTypes = new ArrayList<>();
    }
    
    protected void registerToken(TTokenType name, String pattern) {
        tokenTypes.add(new LexerTokenDefinition<>(name, Pattern.compile("^" + pattern)));
    }
    
    public ArrayList<LexerToken<TTokenType>> lex() {
        while (input.length() > 0) {
            for (LexerTokenDefinition<TTokenType> tt : tokenTypes) {
                if (tt.matches(input)) {
                    LexerToken<TTokenType> t = tt.extract(input);
                    
                    if (t.getType() != whitespaceToken) {
                        tokens.add(t);
                    }
                    
                    input = input.substring(t.getValue().length());
                    break;
                }
            }
        }
        
        tokens.stream()
            .filter(token -> token.getType() == badToken)
            .forEach(token -> System.out.print("[" + token.getType() + " " + token.getValue() + "] "));
        
        tokens.add(new LexerToken<>(badToken, ""));
        
        return tokens;
    }
}

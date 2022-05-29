package sk.o2.lexer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class LexerTokenDefinition<TTokenType> {
    private final TTokenType type;
    private final Pattern pattern;
    
    public LexerTokenDefinition(TTokenType type, Pattern pattern) {
        this.type = type;
        this.pattern = pattern;
    }
    
    public boolean matches(String s) {
        return pattern.matcher(s).find();
    }
    
    public LexerToken<TTokenType> extract(String s) {
        Matcher m = pattern.matcher(s);
        //noinspection ResultOfMethodCallIgnored
        m.find();
        return new LexerToken<>(type, m.group(0));
    }
}

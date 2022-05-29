package sk.o2.lexer;

public final class LexerToken<TTokenEnum> {
    private final TTokenEnum type;
    private final String value;
    
    public LexerToken(TTokenEnum type, String value) {
        this.type = type;
        this.value = value;
    }
    
    public TTokenEnum getType() {
        return type;
    }
    
    public String getValue() {
        return value;
    }
}

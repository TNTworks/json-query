package sk.o2.json.lexer;

public final class JsonToken {
    private final JsonTokenTypeEnum type;
    private final String value;
    
    public JsonToken(JsonTokenTypeEnum type, String value) {
        this.type = type;
        this.value = value;
    }
    
    public JsonTokenTypeEnum getType() {
        return type;
    }
    
    public String getValue() {
        return value;
    }
}

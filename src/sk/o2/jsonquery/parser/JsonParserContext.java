package sk.o2.jsonquery.parser;

public final class JsonParserContext<T extends JQParsedEntity<T>> {
    private final JsonParser parser;
    private final T parsedEntity;
    
    public JsonParserContext(JsonParser parser, T parsedEntity) {
        this.parser = parser;
        this.parsedEntity = parsedEntity;
    }
    
    public JsonParser getParser() {
        return parser;
    }
    
    public T getParsedEntity() {
        return parsedEntity;
    }
}

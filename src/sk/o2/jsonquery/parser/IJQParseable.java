package sk.o2.jsonquery.parser;

public interface IJQParseable<T extends JQParsedEntity<T>> {
    JsonParserContext<T> parse(JsonParser parser);
}

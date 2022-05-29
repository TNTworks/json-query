package sk.o2.json.parser;

public interface IJQParseable<T extends JQParsedEntity<T>> {
    JsonParserContext<T> parse(JsonParser parser);
}

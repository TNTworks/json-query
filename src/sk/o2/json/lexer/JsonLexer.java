package sk.o2.json.lexer;

import sk.o2.lexer.AbstractLexer;

public final class JsonLexer extends AbstractLexer<JsonTokenEnum> {
    public JsonLexer(String input) {
        super(input, JsonTokenEnum.BAD_TOKEN, JsonTokenEnum.EOF, JsonTokenEnum.WHITESPACE);
        
        registerToken(JsonTokenEnum.LEFT_CURLY, "\\{");
        registerToken(JsonTokenEnum.RIGHT_CURLY, "\\}");
        registerToken(JsonTokenEnum.LEFT_SQUARE, "\\[");
        registerToken(JsonTokenEnum.RIGHT_SQUARE, "\\]");
        registerToken(JsonTokenEnum.COMMA, ",");
        registerToken(JsonTokenEnum.COLON, ":");
        registerToken(JsonTokenEnum.STRING, "\"[^\"]*\"");
        registerToken(JsonTokenEnum.NUMBER, "-?\\d+(\\.\\d+)?");
        registerToken(JsonTokenEnum.TRUE, "true");
        registerToken(JsonTokenEnum.FALSE, "false");
        registerToken(JsonTokenEnum.NULL, "null");
        registerToken(JsonTokenEnum.WHITESPACE, "\\s+");
        registerToken(JsonTokenEnum.BAD_TOKEN, ".");
    }
}

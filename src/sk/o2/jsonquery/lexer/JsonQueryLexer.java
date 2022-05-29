package sk.o2.jsonquery.lexer;

import sk.o2.lexer.AbstractLexer;

public final class JsonQueryLexer extends AbstractLexer<JsonQueryLexerTokenEnum> {
    public JsonQueryLexer(String input) {
        super(input, JsonQueryLexerTokenEnum.BAD_TOKEN, JsonQueryLexerTokenEnum.EOF, JsonQueryLexerTokenEnum.WHITESPACE);
        
        registerToken(JsonQueryLexerTokenEnum.LEFT_CURLY, "\\{");
        registerToken(JsonQueryLexerTokenEnum.RIGHT_CURLY, "\\}");
        registerToken(JsonQueryLexerTokenEnum.LEFT_SQUARE, "\\[");
        registerToken(JsonQueryLexerTokenEnum.RIGHT_SQUARE, "\\]");
        registerToken(JsonQueryLexerTokenEnum.COMMA, ",");
        registerToken(JsonQueryLexerTokenEnum.COLON, ":");
        registerToken(JsonQueryLexerTokenEnum.STRING, "\"[^\"]*\"");
        registerToken(JsonQueryLexerTokenEnum.NUMBER, "-?\\d+(\\.\\d+)?");
        registerToken(JsonQueryLexerTokenEnum.TRUE, "true");
        registerToken(JsonQueryLexerTokenEnum.FALSE, "false");
        registerToken(JsonQueryLexerTokenEnum.NULL, "null");
        registerToken(JsonQueryLexerTokenEnum.WHITESPACE, "\\s+");
        registerToken(JsonQueryLexerTokenEnum.BAD_TOKEN, ".");
    }
}

package sk.o2.jsonquery.lexer;

import sk.o2.lexer.AbstractLexer;

public final class JsonQueryLexer extends AbstractLexer<JsonQueryLexerTokenEnum> {
    public JsonQueryLexer(String input) {
        super(input, JsonQueryLexerTokenEnum.BAD_TOKEN, JsonQueryLexerTokenEnum.EOF, JsonQueryLexerTokenEnum.WHITESPACE);
        
        registerToken(JsonQueryLexerTokenEnum.DOUBLE_SLASH, "//");
        registerToken(JsonQueryLexerTokenEnum.SINGLE_SLASH, "/");
        registerToken(JsonQueryLexerTokenEnum.LEFT_SQUARE, "\\[");
        registerToken(JsonQueryLexerTokenEnum.RIGHT_SQUARE, "\\]");
        registerToken(JsonQueryLexerTokenEnum.STRING, "\"[^\"]*\"");
        registerToken(JsonQueryLexerTokenEnum.NUMBER, "-?\\d+(\\.\\d+)?");
        registerToken(JsonQueryLexerTokenEnum.TRUE, "true");
        registerToken(JsonQueryLexerTokenEnum.FALSE, "false");
        registerToken(JsonQueryLexerTokenEnum.IDENTIFIER, "[a-zA-Z_][a-zA-Z0-9_]*");
        registerToken(JsonQueryLexerTokenEnum.DOUBLE_DOT, "\\..");
        registerToken(JsonQueryLexerTokenEnum.DOT, "\\.");
        registerToken(JsonQueryLexerTokenEnum.EQUALS, "=");
        registerToken(JsonQueryLexerTokenEnum.WHITESPACE, "\\s+");
        registerToken(JsonQueryLexerTokenEnum.BAD_TOKEN, ".");
    }
}

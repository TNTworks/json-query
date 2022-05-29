package sk.o2.json.lexer;

import java.util.ArrayList;
import java.util.regex.Pattern;

public final class JsonLexer {
    private String input;
    
    private final ArrayList<JsonToken> tokens;
    private final ArrayList<JsonTokenType> tokenTypes;
    
    public JsonLexer(String input) {
        this.input = input;
        
        tokens = new ArrayList<>();
        tokenTypes = new ArrayList<>();
    
        registerToken(JsonTokenTypeEnum.LEFT_CURLY, "\\{");
        registerToken(JsonTokenTypeEnum.RIGHT_CURLY, "\\}");
        registerToken(JsonTokenTypeEnum.LEFT_SQUARE, "\\[");
        registerToken(JsonTokenTypeEnum.RIGHT_SQUARE, "\\]");
        registerToken(JsonTokenTypeEnum.COMMA, ",");
        registerToken(JsonTokenTypeEnum.COLON, ":");
        registerToken(JsonTokenTypeEnum.STRING, "\"[^\"]*\"");
        registerToken(JsonTokenTypeEnum.NUMBER, "-?\\d+(\\.\\d+)?");
        registerToken(JsonTokenTypeEnum.TRUE, "true");
        registerToken(JsonTokenTypeEnum.FALSE, "false");
        registerToken(JsonTokenTypeEnum.NULL, "null");
        registerToken(JsonTokenTypeEnum.WHITESPACE, "\\s+");
        registerToken(JsonTokenTypeEnum.BAD_TOKEN, ".");
    }
    
    private void registerToken(JsonTokenTypeEnum name, String pattern) {
        tokenTypes.add(new JsonTokenType(name, Pattern.compile("^" + pattern)));
    }
    
    public ArrayList<JsonToken> lex() {
        while (input.length() > 0) {
            for (JsonTokenType tt : tokenTypes) {
                if (tt.matches(input)) {
                    JsonToken t = tt.extract(input);
                    
                    if (t.getType() != JsonTokenTypeEnum.WHITESPACE) {
                        tokens.add(t);
                    }
                    
                    input = input.substring(t.getValue().length());
                    break;
                }
            }
        }
        
        tokens.stream()
            .filter(token -> token.getType() == JsonTokenTypeEnum.BAD_TOKEN)
            .forEach(token -> System.out.print("[" + token.getType() + " " + token.getValue() + "] "));
        
        tokens.add(new JsonToken(JsonTokenTypeEnum.EOF, ""));
        
        return tokens;
    }
}

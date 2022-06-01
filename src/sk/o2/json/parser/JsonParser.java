package sk.o2.json.parser;

import sk.o2.json.lexer.JsonTokenEnum;
import sk.o2.lexer.LexerToken;
import sk.o2.json.ast.JQArray;
import sk.o2.json.ast.JQObject;

import java.util.ArrayList;

public final class JsonParser {
    private final ArrayList<LexerToken<JsonTokenEnum>> tokens;
    
    public JsonParser(ArrayList<LexerToken<JsonTokenEnum>> tokens) {
        this.tokens = tokens;
    }
    
    public IJQPrintable parse() {
        if (isNext(JsonTokenEnum.LEFT_CURLY)) {
            JsonParserContext<JQObject> pc = new JQObject().parse(this);
            
            if (peek().getType() != JsonTokenEnum.EOF) {
                throw new RuntimeException("Unexpected token " + peek().getType());
            }
            
            return pc.getParsedEntity();
        } else if (isNext(JsonTokenEnum.LEFT_SQUARE)) {
            JsonParserContext<JQArray> pc = new JQArray().setArrayName("anonymousRoot").parse(this);
    
            if (peek().getType() != JsonTokenEnum.EOF) {
                throw new RuntimeException("Unexpected token " + peek().getType());
            }
            
            return pc.getParsedEntity();
        }
    
        throw new RuntimeException("Expected [array or object] but found " + peek().getType());
    }
    
    public boolean isNext(JsonTokenEnum type) {
        return tokens.size() > 0 && peek().getType() == type;
    }
    
    public LexerToken<JsonTokenEnum> expect(JsonTokenEnum type) {
        if (!isNext(type)) {
            throw new RuntimeException("Expected " + type + " but found " + peek().getType());
        }
        
        return consume();
    }
    
    public LexerToken<JsonTokenEnum> peek() {
        return tokens.get(0);
    }
    
    public LexerToken<JsonTokenEnum> consume() {
        return tokens.remove(0);
    }
}

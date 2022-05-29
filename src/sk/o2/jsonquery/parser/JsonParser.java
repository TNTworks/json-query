package sk.o2.jsonquery.parser;

import sk.o2.jsonquery.lexer.JsonToken;
import sk.o2.jsonquery.lexer.JsonTokenTypeEnum;
import sk.o2.jsonquery.ast.JQArray;
import sk.o2.jsonquery.ast.JQObject;

import java.util.ArrayList;

public final class JsonParser {
    private final ArrayList<JsonToken> tokens;
    
    public JsonParser(ArrayList<JsonToken> tokens) {
        this.tokens = tokens;
    }
    
    public JQParsedEntity<?> parse() {
        if (isNext(JsonTokenTypeEnum.LEFT_CURLY)) {
            JsonParserContext<JQObject> pc = new JQObject().parse(this);
            
            if (this.peek().getType() != JsonTokenTypeEnum.EOF) {
                throw new RuntimeException("Unexpected token " + this.peek().getType());
            }
            
            return pc.getParsedEntity();
        } else if (isNext(JsonTokenTypeEnum.LEFT_SQUARE)) {
            JsonParserContext<JQArray> pc = new JQArray().parse(this);
    
            if (this.peek().getType() != JsonTokenTypeEnum.EOF) {
                throw new RuntimeException("Unexpected token " + this.peek().getType());
            }
            
            return pc.getParsedEntity();
        }
    
        throw new RuntimeException("Expected [array or object] but found " + this.peek().getType());
    }
    
    public boolean isNext(JsonTokenTypeEnum type) {
        return tokens.size() > 0 && peek().getType() == type;
    }
    
    public JsonToken expect(JsonTokenTypeEnum type) {
        if (!isNext(type)) {
            throw new RuntimeException("Expected " + type + " but found " + peek().getType());
        }
        
        return consume();
    }
    
    public JsonToken peek() {
        return tokens.get(0);
    }
    
    public JsonToken consume() {
        return tokens.remove(0);
    }
}

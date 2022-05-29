package sk.o2.json.ast;

import sk.o2.json.parser.JQParsedEntity;
import sk.o2.json.parser.JsonParser;
import sk.o2.json.parser.JsonParserContext;
import sk.o2.json.lexer.JsonTokenEnum;

public final class JQLiteral extends JQParsedEntity<JQLiteral> {
    private String value;
    
    @Override
    public JsonParserContext<JQLiteral> parse(JsonParser parser) {
        if (parser.isNext(JsonTokenEnum.TRUE)) {
            value = parser.expect(JsonTokenEnum.TRUE).getValue();
        } else if (parser.isNext(JsonTokenEnum.FALSE)) {
            value = parser.expect(JsonTokenEnum.FALSE).getValue();
        } else if (parser.isNext(JsonTokenEnum.STRING)) {
            value = parser.expect(JsonTokenEnum.STRING).getValue();
        } else if (parser.isNext(JsonTokenEnum.NUMBER)) {
            value = parser.expect(JsonTokenEnum.NUMBER).getValue();
        } else if (parser.isNext(JsonTokenEnum.NULL)) {
            value = parser.expect(JsonTokenEnum.NULL).getValue();
        }
        
        return new JsonParserContext<>(parser, this);
    }
    
    @Override
    public String print() {
        return value;
    }
}

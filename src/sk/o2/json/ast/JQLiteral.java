package sk.o2.json.ast;

import sk.o2.json.parser.JQParsedEntity;
import sk.o2.json.parser.JsonParser;
import sk.o2.json.parser.JsonParserContext;
import sk.o2.json.lexer.JsonTokenTypeEnum;

public final class JQLiteral extends JQParsedEntity<JQLiteral> {
    private String value;
    
    @Override
    public JsonParserContext<JQLiteral> parse(JsonParser parser) {
        if (parser.isNext(JsonTokenTypeEnum.TRUE)) {
            value = parser.expect(JsonTokenTypeEnum.TRUE).getValue();
        } else if (parser.isNext(JsonTokenTypeEnum.FALSE)) {
            value = parser.expect(JsonTokenTypeEnum.FALSE).getValue();
        } else if (parser.isNext(JsonTokenTypeEnum.STRING)) {
            value = parser.expect(JsonTokenTypeEnum.STRING).getValue();
        } else if (parser.isNext(JsonTokenTypeEnum.NUMBER)) {
            value = parser.expect(JsonTokenTypeEnum.NUMBER).getValue();
        } else if (parser.isNext(JsonTokenTypeEnum.NULL)) {
            value = parser.expect(JsonTokenTypeEnum.NULL).getValue();
        }
        
        return new JsonParserContext<>(parser, this);
    }
    
    @Override
    public String print() {
        return value;
    }
}

package sk.o2.jsonquery.ast.nodes;

import sk.o2.jsonquery.parser.JQParsedEntity;
import sk.o2.jsonquery.parser.JsonParser;
import sk.o2.jsonquery.parser.JsonParserContext;
import sk.o2.jsonquery.lexer.JsonTokenTypeEnum;

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

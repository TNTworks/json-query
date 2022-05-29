package sk.o2.jsonquery.ast.nodes;

import sk.o2.jsonquery.parser.JQParsedEntity;
import sk.o2.jsonquery.parser.JsonParser;
import sk.o2.jsonquery.parser.JsonParserContext;
import sk.o2.jsonquery.lexer.JsonTokenTypeEnum;

public final class JQProperty extends JQParsedEntity<JQProperty> {
    private String name;
    private JQObject object;
    private JQArray array;
    private JQLiteral literal;
    
    @Override
    public JsonParserContext<JQProperty> parse(JsonParser parser) {
        name = parser.expect(JsonTokenTypeEnum.STRING).getValue();
        parser.expect(JsonTokenTypeEnum.COLON);
        
        if (parser.isNext(JsonTokenTypeEnum.LEFT_SQUARE)) {
            JsonParserContext<JQArray> pc = new JQArray().parse(parser);
            parser = pc.getParser();
            array = pc.getParsedEntity();
        } else if (parser.isNext(JsonTokenTypeEnum.LEFT_CURLY)) {
            JsonParserContext<JQObject> pc = new JQObject().parse(parser);
            parser = pc.getParser();
            object = pc.getParsedEntity();
        } else if (
            parser.isNext(JsonTokenTypeEnum.STRING) ||
            parser.isNext(JsonTokenTypeEnum.NUMBER) ||
            parser.isNext(JsonTokenTypeEnum.TRUE) ||
            parser.isNext(JsonTokenTypeEnum.FALSE) ||
            parser.isNext(JsonTokenTypeEnum.NULL)
        ) {
            JsonParserContext<JQLiteral> pc = new JQLiteral().parse(parser);
            parser = pc.getParser();
            literal = pc.getParsedEntity();
        } else {
            throw new RuntimeException("Expected [array, object or literal] but found " + parser.peek().getType());
        }
        
        return new JsonParserContext<>(parser, this);
    }
    
    @Override
    public String print() {
        return name + ":" + (array != null ? array.print() : (object != null ? object.print() : literal.print()));
    }
}

package sk.o2.jsonquery.ast;

import sk.o2.jsonquery.parser.JQParsedEntity;
import sk.o2.jsonquery.parser.JsonParser;
import sk.o2.jsonquery.parser.JsonParserContext;
import sk.o2.jsonquery.lexer.JsonTokenTypeEnum;

import java.util.ArrayList;

public final class JQArray extends JQParsedEntity<JQArray> {
    private final ArrayList<JQParsedEntity<?>> elements = new ArrayList<>();
    
    @Override
    public JsonParserContext<JQArray> parse(JsonParser parser) {
        parser.expect(JsonTokenTypeEnum.LEFT_SQUARE);
        
        while (
            parser.isNext(JsonTokenTypeEnum.LEFT_SQUARE) ||
            parser.isNext(JsonTokenTypeEnum.LEFT_CURLY) ||
            parser.isNext(JsonTokenTypeEnum.STRING) ||
            parser.isNext(JsonTokenTypeEnum.NUMBER) ||
            parser.isNext(JsonTokenTypeEnum.TRUE) ||
            parser.isNext(JsonTokenTypeEnum.FALSE) ||
            parser.isNext(JsonTokenTypeEnum.NULL)
        ) {
            if (parser.isNext(JsonTokenTypeEnum.LEFT_SQUARE)) {
                JsonParserContext<JQArray> pc = new JQArray().parse(parser);
                parser = pc.getParser();
                elements.add(pc.getParsedEntity());
            } else if (parser.isNext(JsonTokenTypeEnum.LEFT_CURLY)) {
                JsonParserContext<JQObject> pc = new JQObject().parse(parser);
                parser = pc.getParser();
                elements.add(pc.getParsedEntity());
            } else if (
                parser.isNext(JsonTokenTypeEnum.STRING) ||
                parser.isNext(JsonTokenTypeEnum.NUMBER) ||
                parser.isNext(JsonTokenTypeEnum.TRUE) ||
                parser.isNext(JsonTokenTypeEnum.FALSE) ||
                parser.isNext(JsonTokenTypeEnum.NULL)
            ) {
                JsonParserContext<JQLiteral> pc = new JQLiteral().parse(parser);
                parser = pc.getParser();
                elements.add(pc.getParsedEntity());
            }
            
            if (parser.isNext(JsonTokenTypeEnum.COMMA)) {
                parser.expect(JsonTokenTypeEnum.COMMA);
            } else {
                break;
            }
        }
        
        parser.expect(JsonTokenTypeEnum.RIGHT_SQUARE);
        
        return new JsonParserContext<>(parser, this);
    }
    
    @Override
    public String print() {
        return "[" + String.join(",", elements.stream().map(JQParsedEntity::print).toArray(String[]::new)) + "]";
    }
}

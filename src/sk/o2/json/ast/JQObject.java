package sk.o2.json.ast;

import sk.o2.json.parser.IJQPrintable;
import sk.o2.json.parser.JQParsedEntity;
import sk.o2.json.parser.JsonParser;
import sk.o2.json.parser.JsonParserContext;
import sk.o2.json.lexer.JsonTokenTypeEnum;

import java.util.ArrayList;

public final class JQObject extends JQParsedEntity<JQObject> {
    private final ArrayList<JQProperty> properties = new ArrayList<>();
    
    public JsonParserContext<JQObject> parse(JsonParser parser) {
        parser.expect(JsonTokenTypeEnum.LEFT_CURLY);
        
        while (parser.isNext(JsonTokenTypeEnum.STRING)) {
            JsonParserContext<JQProperty> pc = new JQProperty().parse(parser);
            parser = pc.getParser();
            properties.add(pc.getParsedEntity());
            
            if (parser.isNext(JsonTokenTypeEnum.COMMA)) {
                parser.expect(JsonTokenTypeEnum.COMMA);
            } else {
                break;
            }
        }
        
        parser.expect(JsonTokenTypeEnum.RIGHT_CURLY);
        
        return new JsonParserContext<>(parser, this);
    }
    
    @Override
    public String print() {
        return "{" +
               String.join(",", properties
                   .stream()
                   .map(IJQPrintable::print)
                   .toArray(String[]::new)) +
               "}";
    }
}

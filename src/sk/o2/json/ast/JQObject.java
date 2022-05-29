package sk.o2.json.ast;

import sk.o2.json.parser.IJQPrintable;
import sk.o2.json.parser.JQParsedEntity;
import sk.o2.json.parser.JsonParser;
import sk.o2.json.parser.JsonParserContext;
import sk.o2.json.lexer.JsonTokenEnum;

import java.util.ArrayList;

public final class JQObject extends JQParsedEntity<JQObject> {
    private final ArrayList<JQProperty> properties = new ArrayList<>();
    
    public JsonParserContext<JQObject> parse(JsonParser parser) {
        parser.expect(JsonTokenEnum.LEFT_CURLY);
        
        while (parser.isNext(JsonTokenEnum.STRING)) {
            JsonParserContext<JQProperty> pc = new JQProperty().parse(parser);
            parser = pc.getParser();
            properties.add(pc.getParsedEntity());
            
            if (parser.isNext(JsonTokenEnum.COMMA)) {
                parser.expect(JsonTokenEnum.COMMA);
            } else {
                break;
            }
        }
        
        parser.expect(JsonTokenEnum.RIGHT_CURLY);
        
        return new JsonParserContext<>(parser, this);
    }
    
    @Override
    public String print() {
        return
            "{" +
            String.join(",", properties
                .stream()
                .map(IJQPrintable::print)
                .toArray(String[]::new)) +
            "}";
    }
    
    @Override
    public String printXml() {
        return String.join("", properties
            .stream()
            .map(IJQPrintable::printXml)
            .toArray(String[]::new));
    }
}

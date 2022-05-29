package sk.o2.json.ast;

import sk.o2.json.parser.JQParsedEntity;
import sk.o2.json.parser.JsonParser;
import sk.o2.json.parser.JsonParserContext;
import sk.o2.json.lexer.JsonTokenEnum;

import java.util.ArrayList;

public final class JQArray extends JQParsedEntity<JQArray> {
    private String arrayName;
    private final ArrayList<JQParsedEntity<?>> elements = new ArrayList<>();
    
    @Override
    public JsonParserContext<JQArray> parse(JsonParser parser) {
        parser.expect(JsonTokenEnum.LEFT_SQUARE);
        
        while (
            parser.isNext(JsonTokenEnum.LEFT_SQUARE) ||
            parser.isNext(JsonTokenEnum.LEFT_CURLY) ||
            parser.isNext(JsonTokenEnum.STRING) ||
            parser.isNext(JsonTokenEnum.NUMBER) ||
            parser.isNext(JsonTokenEnum.TRUE) ||
            parser.isNext(JsonTokenEnum.FALSE) ||
            parser.isNext(JsonTokenEnum.NULL)
        ) {
            if (parser.isNext(JsonTokenEnum.LEFT_SQUARE)) {
                JsonParserContext<JQArray> pc = new JQArray().setArrayName(arrayName).parse(parser);
                parser = pc.getParser();
                elements.add(pc.getParsedEntity());
            } else if (parser.isNext(JsonTokenEnum.LEFT_CURLY)) {
                JsonParserContext<JQObject> pc = new JQObject().parse(parser);
                parser = pc.getParser();
                elements.add(pc.getParsedEntity());
            } else if (
                parser.isNext(JsonTokenEnum.STRING) ||
                parser.isNext(JsonTokenEnum.NUMBER) ||
                parser.isNext(JsonTokenEnum.TRUE) ||
                parser.isNext(JsonTokenEnum.FALSE) ||
                parser.isNext(JsonTokenEnum.NULL)
            ) {
                JsonParserContext<JQLiteral> pc = new JQLiteral().parse(parser);
                parser = pc.getParser();
                elements.add(pc.getParsedEntity());
            }
            
            if (parser.isNext(JsonTokenEnum.COMMA)) {
                parser.expect(JsonTokenEnum.COMMA);
            } else {
                break;
            }
        }
        
        parser.expect(JsonTokenEnum.RIGHT_SQUARE);
        
        return new JsonParserContext<>(parser, this);
    }
    
    @Override
    public String print() {
        return "[" + String.join(",", elements.stream().map(JQParsedEntity::print).toArray(String[]::new)) + "]";
    }
    
    @Override
    public String printXml() {
        return
            String.join("", elements.stream()
                .map(JQParsedEntity::printXml)
                .map(e -> "<" + arrayName + ">" + e + "</" + arrayName + ">")
                .toArray(String[]::new));
    }
    
    public JQArray setArrayName(String arrayName) {
        this.arrayName =
            (arrayName.startsWith("\"") && arrayName.endsWith("\""))
                ? arrayName.substring(1, arrayName.length() - 1)
                : arrayName;
        return this;
    }
}

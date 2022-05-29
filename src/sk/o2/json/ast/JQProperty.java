package sk.o2.json.ast;

import sk.o2.json.parser.JQParsedEntity;
import sk.o2.json.parser.JsonParser;
import sk.o2.json.parser.JsonParserContext;
import sk.o2.json.lexer.JsonTokenEnum;

public final class JQProperty extends JQParsedEntity<JQProperty> {
    private String name;
    private JQObject object;
    private JQArray array;
    private JQLiteral literal;
    
    @Override
    public JsonParserContext<JQProperty> parse(JsonParser parser) {
        name = parser.expect(JsonTokenEnum.STRING).getValue();
        parser.expect(JsonTokenEnum.COLON);
        
        if (parser.isNext(JsonTokenEnum.LEFT_SQUARE)) {
            JsonParserContext<JQArray> pc = new JQArray().setArrayName(name).parse(parser);
            parser = pc.getParser();
            array = pc.getParsedEntity();
        } else if (parser.isNext(JsonTokenEnum.LEFT_CURLY)) {
            JsonParserContext<JQObject> pc = new JQObject().parse(parser);
            parser = pc.getParser();
            object = pc.getParsedEntity();
        } else if (
            parser.isNext(JsonTokenEnum.STRING) ||
            parser.isNext(JsonTokenEnum.NUMBER) ||
            parser.isNext(JsonTokenEnum.TRUE) ||
            parser.isNext(JsonTokenEnum.FALSE) ||
            parser.isNext(JsonTokenEnum.NULL)
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
        return name + ":" + (
            array != null
                ? array.print()
                : (
                    object != null
                        ? object.print()
                        : literal.print()
                )
        );
    }
    
    @Override
    public String printXml() {
        return (
            array != null
                ? array.printXml()
                : (
                    literal != null ?
                        (
                            literal.isNull() || literal.isEmpty() ?
                                "<" + name.substring(1, name.length() - 1) + "/>" :
                                "<" + name.substring(1, name.length() - 1) + ">" +
                                literal.printXml() +
                                "</" + name.substring(1, name.length() - 1) + ">"
                        ) :
                        "<" + name.substring(1, name.length() - 1) + ">" +
                        object.printXml() +
                        "</" + name.substring(1, name.length() - 1) + ">"
                )
        );
    }
}

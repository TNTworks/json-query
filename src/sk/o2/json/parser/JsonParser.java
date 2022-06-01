package sk.o2.json.parser;

import sk.o2.json.lexer.JsonTokenEnum;
import sk.o2.lexer.LexerToken;
import sk.o2.parser.AbstractParser;
import sk.o2.parser.ParserNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public final class JsonParser extends AbstractParser<JsonTokenEnum, JsonNodeEnum> {
    private final HashMap<JsonTokenEnum, JsonNodeEnum> mappings;
    
    public JsonParser(ArrayList<LexerToken<JsonTokenEnum>> arrayList) {
        super(arrayList, JsonNodeEnum.ROOT);
        
        mappings = new HashMap<>();
        
        mappings.put(JsonTokenEnum.LEFT_CURLY, JsonNodeEnum.OBJECT);
        mappings.put(JsonTokenEnum.STRING, JsonNodeEnum.STRING);
        mappings.put(JsonTokenEnum.NUMBER, JsonNodeEnum.NUMBER);
        mappings.put(JsonTokenEnum.TRUE, JsonNodeEnum.TRUE);
        mappings.put(JsonTokenEnum.FALSE, JsonNodeEnum.FALSE);
        mappings.put(JsonTokenEnum.NULL, JsonNodeEnum.NULL);
        
        addParsingLogic(JsonNodeEnum.ROOT, (context) -> {
            if (context.isNext(JsonTokenEnum.LEFT_CURLY)) {
                context = context.parse(JsonNodeEnum.OBJECT, "root");
                
                if (context.peek().getType() != JsonTokenEnum.EOF) {
                    throw new RuntimeException("Unexpected token " + context.peek().getType());
                }
                
                return context;
            } else if (context.isNext(JsonTokenEnum.LEFT_SQUARE)) {
                context = context.parse(JsonNodeEnum.ARRAY, "root");
                
                if (context.peek().getType() != JsonTokenEnum.EOF) {
                    throw new RuntimeException("Unexpected token " + context.peek().getType());
                }
                
                return context;
            }
            
            throw new RuntimeException("Expected [array or object] but found " + context.peek().getType());
        });
        
        addParsingLogic(JsonNodeEnum.ARRAY, (context) -> {
            context.expect(JsonTokenEnum.LEFT_SQUARE);
            
            ParserNode<JsonNodeEnum> node = new ParserNode<>(JsonNodeEnum.ARRAY);
            
            while (
                context.isNext(new ArrayList<>(Arrays.asList(
                    JsonTokenEnum.LEFT_SQUARE,
                    JsonTokenEnum.LEFT_CURLY,
                    JsonTokenEnum.STRING,
                    JsonTokenEnum.NUMBER,
                    JsonTokenEnum.TRUE,
                    JsonTokenEnum.FALSE,
                    JsonTokenEnum.NULL
                )))
            ) {
                if (context.isNext(JsonTokenEnum.LEFT_SQUARE)) {
                    context = context.parse(JsonNodeEnum.ARRAY, context.getNode().getName());
                    node.withChild(context.getNode());
                } else {
                    for (JsonTokenEnum type : mappings.keySet()) {
                        if (context.isNext(type)) {
                            context = context.parse(mappings.get(type));
                            node.withChild(context.getNode());
                            break;
                        }
                    }
                }
                
                if (context.isNext(JsonTokenEnum.COMMA)) {
                    context.expect(JsonTokenEnum.COMMA);
                } else {
                    break;
                }
            }
            
            context.expect(JsonTokenEnum.RIGHT_SQUARE);
            
            return context.withNode(node);
        });
        
        addParsingLogic(JsonNodeEnum.TRUE, (context) -> context.withNode(new ParserNode<>(JsonNodeEnum.TRUE)
            .withValue(context.expect(JsonTokenEnum.TRUE).getValue())));
        
        addParsingLogic(JsonNodeEnum.FALSE, (context) -> context.withNode(new ParserNode<>(JsonNodeEnum.FALSE)
            .withValue(context.expect(JsonTokenEnum.FALSE).getValue())));
        
        addParsingLogic(JsonNodeEnum.STRING, (context) -> context.withNode(new ParserNode<>(JsonNodeEnum.STRING)
            .withValue(context.expect(JsonTokenEnum.STRING).getValue())));
        
        addParsingLogic(JsonNodeEnum.NUMBER, (context) -> context.withNode(new ParserNode<>(JsonNodeEnum.NUMBER)
            .withValue(context.expect(JsonTokenEnum.NUMBER).getValue())));
        
        addParsingLogic(JsonNodeEnum.NULL, (context) -> context.withNode(new ParserNode<>(JsonNodeEnum.NULL)
            .withValue(context.expect(JsonTokenEnum.NULL).getValue())));
        
        addParsingLogic(JsonNodeEnum.OBJECT, (context) -> {
            context.expect(JsonTokenEnum.LEFT_CURLY);
            
            ParserNode<JsonNodeEnum> node = new ParserNode<>(JsonNodeEnum.OBJECT);
            
            while (context.isNext(JsonTokenEnum.STRING)) {
                context = context.parse(JsonNodeEnum.PROPERTY);
                node.withChild(context.getNode());
                
                if (context.isNext(JsonTokenEnum.COMMA)) {
                    context.expect(JsonTokenEnum.COMMA);
                } else {
                    break;
                }
            }
            
            context.expect(JsonTokenEnum.RIGHT_CURLY);
            
            return context.withNode(node);
        });
        
        addParsingLogic(JsonNodeEnum.PROPERTY, (context) -> {
            String name = context.expect(JsonTokenEnum.STRING).getValue();
            context.expect(JsonTokenEnum.COLON);
            
            ParserNode<JsonNodeEnum> node = new ParserNode<>(name, JsonNodeEnum.PROPERTY);
            
            if (context.isNext(JsonTokenEnum.LEFT_SQUARE)) {
                context = context.parse(JsonNodeEnum.ARRAY, name);
                node.withChild(context.getNode());
            } else if (context.isNext(JsonTokenEnum.LEFT_CURLY)) {
                context = context.parse(JsonNodeEnum.OBJECT);
                node.withChild(context.getNode());
            } else if (
                context.isNext(new ArrayList<>(Arrays.asList(
                    JsonTokenEnum.STRING,
                    JsonTokenEnum.NUMBER,
                    JsonTokenEnum.TRUE,
                    JsonTokenEnum.FALSE,
                    JsonTokenEnum.NULL
                )))
            ) {
                for (JsonTokenEnum type : mappings.keySet()) {
                    if (context.isNext(type)) {
                        context = context.parse(mappings.get(type));
                        node.withChild(context.getNode());
                        break;
                    }
                }
            } else {
                throw new RuntimeException("Expected [array, object or literal] but found " + context.peek().getType());
            }
            
            return context.withNode(node);
        });
    }
}

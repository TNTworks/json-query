package sk.o2.json.parser;

import sk.o2.json.lexer.JsonTokenEnum;
import sk.o2.lexer.LexerToken;
import sk.o2.parser.AbstractParser;
import sk.o2.parser.ParserNode;

import java.util.ArrayList;

public class JsonParserNew extends AbstractParser<JsonTokenEnum, JsonNodeEnum> {
    public JsonParserNew(ArrayList<LexerToken<JsonTokenEnum>> arrayList) {
        super(arrayList, JsonNodeEnum.ROOT);
        
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
                context.isNext(JsonTokenEnum.LEFT_SQUARE) ||
                context.isNext(JsonTokenEnum.LEFT_CURLY) ||
                context.isNext(JsonTokenEnum.STRING) ||
                context.isNext(JsonTokenEnum.NUMBER) ||
                context.isNext(JsonTokenEnum.TRUE) ||
                context.isNext(JsonTokenEnum.FALSE) ||
                context.isNext(JsonTokenEnum.NULL)
            ) {
                if (context.isNext(JsonTokenEnum.LEFT_SQUARE)) {
                    context = context.parse(JsonNodeEnum.ARRAY, context.getNode().getName());
                    node.withChild(context.getNode());
                } else if (context.isNext(JsonTokenEnum.LEFT_CURLY)) {
                    context = context.parse(JsonNodeEnum.OBJECT);
                    node.withChild(context.getNode());
                } else if (
                    context.isNext(JsonTokenEnum.STRING) ||
                    context.isNext(JsonTokenEnum.NUMBER) ||
                    context.isNext(JsonTokenEnum.TRUE) ||
                    context.isNext(JsonTokenEnum.FALSE) ||
                    context.isNext(JsonTokenEnum.NULL)
                ) {
                    context = context.parse(JsonNodeEnum.LITERAL);
                    node.withChild(context.getNode());
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
        
        addParsingLogic(JsonNodeEnum.LITERAL, (context) -> {
            if (context.isNext(JsonTokenEnum.TRUE)) {
                return context.withNode(new ParserNode<>(JsonNodeEnum.LITERAL)
                    .withValue(context.expect(JsonTokenEnum.TRUE).getValue()));
            } else if (context.isNext(JsonTokenEnum.FALSE)) {
                return context.withNode(new ParserNode<>(JsonNodeEnum.LITERAL)
                    .withValue(context.expect(JsonTokenEnum.FALSE).getValue()));
            } else if (context.isNext(JsonTokenEnum.STRING)) {
                return context.withNode(new ParserNode<>(JsonNodeEnum.LITERAL)
                    .withValue(context.expect(JsonTokenEnum.STRING).getValue()));
            } else if (context.isNext(JsonTokenEnum.NUMBER)) {
                return context.withNode(new ParserNode<>(JsonNodeEnum.LITERAL)
                    .withValue(context.expect(JsonTokenEnum.NUMBER).getValue()));
            } else if (context.isNext(JsonTokenEnum.NULL)) {
                return context.withNode(new ParserNode<>(JsonNodeEnum.LITERAL)
                    .withValue(context.expect(JsonTokenEnum.NULL).getValue()));
            }
            
            return context.withNode(new ParserNode<>(JsonNodeEnum.LITERAL));
        });
        
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
                context.isNext(JsonTokenEnum.STRING) ||
                context.isNext(JsonTokenEnum.NUMBER) ||
                context.isNext(JsonTokenEnum.TRUE) ||
                context.isNext(JsonTokenEnum.FALSE) ||
                context.isNext(JsonTokenEnum.NULL)
            ) {
                context = context.parse(JsonNodeEnum.LITERAL);
                node.withChild(context.getNode());
            } else {
                throw new RuntimeException("Expected [array, object or literal] but found " + context.peek().getType());
            }
            
            return context.withNode(node);
        });
    }
}

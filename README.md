# Json Query

**Status**: WIP

Java library that lexes, parses and serializes JSON.

Java library that processes XPath-like query language for JSON.

## Features

This repository contains 3 abstract classes that covers the respective features:
- AbstractLexer<TTokenType>
- AbstractParser<TTokenType, TNodeType>
- AbstractSerializer<TNodeType>

# Basic Usage

Statement `new XmlSerializer(new JsonParser(new JsonLexer(data).lex()).parse()).serialize()` will lex, parse and serialize a JSON string into an XML string.

*Input JSON:*

```json
{
  "subscriber": {
    "subscriberType": "prepaid",
    "subscriberId": 10000000,
    "msisdn": 949000000,
    "tariff": {
      "tariffId": "O2:SK:PREPAID:TARIFF:1",
      "tariffName": "Basic Tariff",
      "tariffType": 10
    }
  }
}

```

*Output XML:*

```xml
<subscriber>
	<subscriberType>prepaid</subscriberType>
	<subscriberId>10000000</subscriberId>
	<msisdn>949000000</msisdn>
	<tariff>
		<tariffId>O2:SK:PREPAID:TARIFF:1</tariffId>
		<tariffName>Basic Tariff</tariffName>
		<tariffType>10</tariffType>
	</tariff>
</subscriber>
```

# AbstractLexer<TTokenType>

Responsible for lexing plain text into predefined tokens.

Example implementation down bellow or also included in this repository can be seen here or in `src/sk/o2/json/lexer/JsonLexer.java`

```java
package sk.o2.json.lexer;

import sk.o2.lexer.AbstractLexer;

public final class JsonLexer extends AbstractLexer<JsonTokenEnum> {
    public JsonLexer(String input) {
        super(input, JsonTokenEnum.BAD_TOKEN, JsonTokenEnum.EOF, JsonTokenEnum.WHITESPACE);
        
        registerToken(JsonTokenEnum.LEFT_CURLY, "\\{");
        registerToken(JsonTokenEnum.RIGHT_CURLY, "\\}");
        registerToken(JsonTokenEnum.LEFT_SQUARE, "\\[");
        registerToken(JsonTokenEnum.RIGHT_SQUARE, "\\]");
        registerToken(JsonTokenEnum.COMMA, ",");
        registerToken(JsonTokenEnum.COLON, ":");
        registerToken(JsonTokenEnum.STRING, "\"[^\"]*\"");
        registerToken(JsonTokenEnum.NUMBER, "-?\\d+(\\.\\d+)?");
        registerToken(JsonTokenEnum.TRUE, "true");
        registerToken(JsonTokenEnum.FALSE, "false");
        registerToken(JsonTokenEnum.NULL, "null");
        registerToken(JsonTokenEnum.WHITESPACE, "\\s+");
        registerToken(JsonTokenEnum.BAD_TOKEN, ".");
    }
}
```

# AbstractParser<TTokenType, TNodeType>

Responsible for parsing list of predefined tokens generated by the lexer into an abstract syntax tree (AST).

Example implementation down bellow or also included in this repository can be seen here or in `src/sk/o2/json/parser/JsonParser.java`

````java
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
````

# AbstractSerializer<TNodeType>

Responsible for serializing list of an abstract syntax tree (AST) generated by the parser to your defined format.

Example implementation of a JSON serializer down bellow or also included in this repository can be seen here or in `src/sk/o2/json/serializer/JsonSerializer.java`

```java
package sk.o2.json.serializer;

import sk.o2.json.parser.JsonNodeEnum;
import sk.o2.parser.ParserNode;
import sk.o2.serializer.AbstractSerializer;

public final class JsonSerializer extends AbstractSerializer<JsonNodeEnum> {
    public JsonSerializer(ParserNode<JsonNodeEnum> rootNode) {
        super(rootNode);
        
        addSerializingLogic(JsonNodeEnum.OBJECT, (context) ->
            context.withSerializedString("{" + String.join(",", context.serializeChildren()) + "}"));
        
        addSerializingLogic(JsonNodeEnum.PROPERTY, (context) ->
            context.withSerializedString(context.getNode().getName() + ":" + context.serializeChild())
        );
        
        addSerializingLogic(JsonNodeEnum.TRUE, (context) ->
            context.withSerializedString(context.getNode().getValue())
        );
        
        addSerializingLogic(JsonNodeEnum.FALSE, (context) ->
            context.withSerializedString(context.getNode().getValue())
        );
        
        addSerializingLogic(JsonNodeEnum.STRING, (context) ->
            context.withSerializedString(context.getNode().getValue())
        );
        
        addSerializingLogic(JsonNodeEnum.NULL, (context) ->
            context.withSerializedString(context.getNode().getValue())
        );
        
        addSerializingLogic(JsonNodeEnum.NUMBER, (context) ->
            context.withSerializedString(context.getNode().getValue())
        );
        
        addSerializingLogic(JsonNodeEnum.ARRAY, (context) ->
            context.withSerializedString("[" + String.join(",", context.serializeChildren()) + "]")
        );
    }
}
```
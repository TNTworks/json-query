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

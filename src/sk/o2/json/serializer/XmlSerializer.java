package sk.o2.json.serializer;

import sk.o2.json.parser.JsonNodeEnum;
import sk.o2.parser.ParserNode;
import sk.o2.serializer.AbstractSerializer;
import sk.o2.serializer.SerializerContext;

public final class XmlSerializer extends AbstractSerializer<JsonNodeEnum> {
    public XmlSerializer(ParserNode<JsonNodeEnum> rootNode) {
        super(rootNode);
        
        addSerializingLogic(JsonNodeEnum.OBJECT, (context) ->
            {
                System.out.println("Serializing object: " + context.toString() + "\n");
                return context.withSerializedString(String.join("", context.serializeChildren()));
            }
        );
        
        addSerializingLogic(JsonNodeEnum.PROPERTY, (context) ->
            {
                System.out.println("Serializing property: " + context.toString() + "\n");
                SerializerContext<JsonNodeEnum> childContext = context.serializeChildToContext();
                
                String nodeName =
                    context.getNode().getName() != null ? context.getNode()
                        .getName().substring(1, context.getNode().getName().length() - 1) : "";
                
                switch (childContext.getNode().getType()) {
                    case ARRAY:
                        return context.withSerializedString(childContext.getSerializedString());
                    case OBJECT:
                        return context.withSerializedString(
                            "<" + nodeName + ">" +
                            childContext.getSerializedString() +
                            "</" + nodeName + ">"
                        );
                    case TRUE:
                    case FALSE:
                    case STRING:
                    case NULL:
                    case NUMBER:
                        if (childContext.getSerializedString().isEmpty()) {
                            return context.withSerializedString("<" + nodeName + "/>");
                        } else {
                            return context.withSerializedString(
                                "<" + nodeName + ">" +
                                childContext.getSerializedString() +
                                "</" + nodeName + ">"
                            );
                        }
                    default:
                        throw new RuntimeException(
                            "Unexpected node type inside property: " + childContext.getNode().getType()
                        );
                }
            }
        );
        
        addSerializingLogic(JsonNodeEnum.TRUE, (context) ->
            context.withSerializedString(context.getNode().getValue())
        );
        
        addSerializingLogic(JsonNodeEnum.FALSE, (context) ->
            context.withSerializedString(context.getNode().getValue())
        );
        
        addSerializingLogic(JsonNodeEnum.STRING, (context) ->
            context.withSerializedString(context.getNode()
                .getValue()
                .substring(1, context.getNode().getValue().length() - 1)
            )
        );
        
        addSerializingLogic(JsonNodeEnum.NULL, (context) ->
            context.withSerializedString("")
        );
        
        addSerializingLogic(JsonNodeEnum.NUMBER, (context) ->
            context.withSerializedString(context.getNode().getValue())
        );
        
        addSerializingLogic(JsonNodeEnum.ARRAY, (context) ->
            {
                System.out.println("Serializing array: " + context.toString() + "\n");
                String nodeName =
                    context.getNode().getName() != null ? context.getNode()
                        .getName().substring(1, context.getNode().getName().length() - 1) : "";
                
                return context.withSerializedString(
                    String.join("", context.serializeChildrenToContext()
                        .stream()
                        .map(e ->
                            "<" + nodeName + ">" +
                            e.getSerializedString() +
                            "</" + nodeName + ">")
                        .toArray(String[]::new))
                );
            }
        );
    }
}

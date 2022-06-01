package sk.o2.serializer;

import sk.o2.parser.ParserNode;

import java.util.function.Function;

public abstract class AbstractSerializer<TNodeType> {
    private final SerializerContext<TNodeType> rootContext;
    
    public AbstractSerializer(ParserNode<TNodeType> node) {
        rootContext = new SerializerContext<>(node);
    }
    
    protected void addSerializingLogic(
        TNodeType node, Function<SerializerContext<TNodeType>, SerializerContext<TNodeType>> logic
    ) {
        rootContext.addSerializingLogic(node, logic);
    }
    
    public String serialize() {
        return rootContext.serialize().getSerializedString();
    }
}
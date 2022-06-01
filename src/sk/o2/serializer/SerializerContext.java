package sk.o2.serializer;

import sk.o2.parser.ParserNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class SerializerContext<TNodeType> {
    private String serializedString;
    
    private final ParserNode<TNodeType> node;
    private final HashMap<TNodeType, Function<SerializerContext<TNodeType>,
        SerializerContext<TNodeType>>> serializingLogic;
    
    public SerializerContext(ParserNode<TNodeType> node) {
        this.node = node;
        this.serializingLogic = new HashMap<>();
    }
    
    private SerializerContext(
        ParserNode<TNodeType> node, HashMap<TNodeType,
        Function<SerializerContext<TNodeType>, SerializerContext<TNodeType>>> serializingLogic
    ) {
        this.node = node;
        this.serializingLogic = serializingLogic;
    }
    
    public SerializerContext<TNodeType> withNode(ParserNode<TNodeType> node) {
        return new SerializerContext<>(node, serializingLogic);
    }
    
    public void addSerializingLogic(
        TNodeType node, Function<SerializerContext<TNodeType>,
        SerializerContext<TNodeType>> logic
    ) {
        serializingLogic.put(node, logic);
    }
    
    public List<SerializerContext<TNodeType>> serializeChildrenToContext() {
        return node.getElements()
            .stream()
            .map(element -> withNode(element).serialize())
            .collect(Collectors.toCollection(ArrayList<SerializerContext<TNodeType>>::new));
    }
    
    public SerializerContext<TNodeType> serializeChildToContext() {
        Optional<ParserNode<TNodeType>> optional = node.getElements().stream().findFirst();
        
        if (!optional.isPresent()) {
            throw new RuntimeException();
        }
    
        Optional<SerializerContext<TNodeType>> optional2 = optional.map(element -> withNode(element).serialize());
    
        if (!optional2.isPresent()) {
            throw new RuntimeException();
        }
        
        return optional2.get();
    }
    
    public String[] serializeChildren() {
        return serializeChildrenToContext().stream().map(SerializerContext::getSerializedString).toArray(String[]::new);
    }
    
    public String serializeChild() {
        return serializeChildToContext().getSerializedString();
    }
    
    public SerializerContext<TNodeType> serialize() {
        if (!serializingLogic.containsKey(node.getType())) {
            throw new RuntimeException("Serializing logic not registered for node: " + node.getType());
        }
        
        return serializingLogic.get(node.getType()).apply(this);
    }
    
    public ParserNode<TNodeType> getNode() {
        return node;
    }
    
    public String getSerializedString() {
        return serializedString;
    }
    
    public SerializerContext<TNodeType> withSerializedString(String serializedString) {
        return new SerializerContext<>(node, serializingLogic).setSerializedString(serializedString);
    }
    
    private SerializerContext<TNodeType> setSerializedString(String serializedString) {
        this.serializedString = serializedString;
        return this;
    }
}

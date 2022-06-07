package sk.o2.serializer;

import sk.o2.parser.ParserNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class SerializerContext<TNodeType> {
    private final ParserNode<TNodeType> node;
    private final HashMap<String, Object> metadata;
    private final HashMap<TNodeType, Function<SerializerContext<TNodeType>,
        SerializerContext<TNodeType>>> serializingLogic;
    private String serializedString;
    
    public SerializerContext(ParserNode<TNodeType> node) {
        this.node = node;
        this.metadata = new HashMap<>();
        this.serializingLogic = new HashMap<>();
    }
    
    private SerializerContext(
        ParserNode<TNodeType> node, HashMap<TNodeType,
        Function<SerializerContext<TNodeType>, SerializerContext<TNodeType>>> serializingLogic
    ) {
        this(node);
        this.serializingLogic.putAll(serializingLogic);
    }
    
    @Override
    public String toString() {
        return "SerializerContext{" +
               "node=" + node +
               '}';
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
    
    private SerializerContext<TNodeType> setSerializedString(String serializedString) {
        this.serializedString = serializedString;
        return this;
    }
    
    public SerializerContext<TNodeType> withSerializedString(String serializedString) {
        return new SerializerContext<>(node, serializingLogic).setSerializedString(serializedString);
    }
    
    public SerializerContext<TNodeType> withMetadata(HashMap<String, Object> data) {
        metadata.putAll(data);
        return this;
    }
    
    public SerializerContext<TNodeType> withMetadata(String key, Object value) {
        metadata.put(key, value);
        return this;
    }
    
    public Object getMetadata(String key) {
        return metadata.getOrDefault(key, new Object());
    }
}

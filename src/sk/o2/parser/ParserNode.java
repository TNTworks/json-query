package sk.o2.parser;

import java.util.ArrayList;

public final class ParserNode<TNodeType> {
    private String value;
    private String name;
    
    private final TNodeType type;
    private final ArrayList<ParserNode<TNodeType>> children;
    
    public ParserNode(TNodeType type) {
        this.type = type;
        
        value = null;
        children = new ArrayList<>();
    }
    
    public ParserNode(String name, TNodeType type) {
        this(type);
        this.name = name;
    }
    
    public ParserNode<TNodeType> withValue(String value) {
        this.value = value;
        return this;
    }
    
    public void withName(String name) {
        this.name = name;
    }
    
    public ParserNode<TNodeType> withChild(ParserNode<TNodeType> child) {
        children.add(child);
        return this;
    }
    
    public boolean hasChildren() {
        return !children.isEmpty();
    }
    
    public boolean isLeaf() {
        return children.isEmpty();
    }
    
    public String getName() {
        return name;
    }
    
    public String getValue() {
        return value;
    }
    
    public TNodeType getType() {
        return type;
    }
    
    public ArrayList<ParserNode<TNodeType>> getElements() {
        return children;
    }
}

package sk.o2.parser;

import sk.o2.lexer.LexerToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;

public class ParserContext<TTokenType, TNodeType> {
    private ParserNode<TNodeType> parsedNode;
    
    private final ArrayList<LexerToken<TTokenType>> tokens;
    private final HashMap<TNodeType, Function<ParserContext<TTokenType, TNodeType>,
        ParserContext<TTokenType, TNodeType>>> parseLogic;
    
    public ParserContext(ArrayList<LexerToken<TTokenType>> tokens) {
        this.tokens = tokens;
        this.parseLogic = new HashMap<>();
    }
    
    private ParserContext(
        ArrayList<LexerToken<TTokenType>> tokens, HashMap<TNodeType,
        Function<ParserContext<TTokenType, TNodeType>, ParserContext<TTokenType, TNodeType>>> parseLogic
    ) {
        this.tokens = tokens;
        this.parseLogic = parseLogic;
    }
    
    public ParserContext<TTokenType, TNodeType> withNode(ParserNode<TNodeType> node) {
        return new ParserContext<>(tokens, parseLogic).setNode(node);
    }
    
    public void addParsingLogic(
        TNodeType node, Function<ParserContext<TTokenType, TNodeType>,
        ParserContext<TTokenType, TNodeType>> logic
    ) {
        parseLogic.put(node, logic);
    }
    
    public boolean isNext(TTokenType type) {
        return tokens.size() > 0 && peek().getType() == type;
    }
    
    public LexerToken<TTokenType> expect(TTokenType type) {
        if (!isNext(type)) {
            throw new RuntimeException("Expected " + type + " but found " + peek().getType());
        }
        
        return consume();
    }
    
    public LexerToken<TTokenType> peek() {
        return tokens.get(0);
    }
    
    public LexerToken<TTokenType> consume() {
        return tokens.remove(0);
    }
    
    public ParserContext<TTokenType, TNodeType> parse(TNodeType node) {
        return parseLogic.get(node).apply(this);
    }
    
    public ParserContext<TTokenType, TNodeType> parse(TNodeType node, String name) {
        ParserContext<TTokenType, TNodeType> ctx = parse(node);
        ctx.getNode().withName(name);
        return ctx;
    }
    
    public ParserNode<TNodeType> getNode() {
        return parsedNode;
    }
    
    private ParserContext<TTokenType, TNodeType> setNode(ParserNode<TNodeType> node) {
        this.parsedNode = node;
        return this;
    }
}

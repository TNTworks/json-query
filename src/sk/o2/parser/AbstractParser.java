package sk.o2.parser;

import sk.o2.lexer.LexerToken;

import java.util.ArrayList;
import java.util.function.Function;

public abstract class AbstractParser<TTokenType, TNodeType> {
    private final TNodeType rootNode;
    private final ParserContext<TTokenType, TNodeType> rootContext;
    
    public AbstractParser(ArrayList<LexerToken<TTokenType>> tokens, TNodeType rootNode) {
        rootContext = new ParserContext<>(tokens);
        this.rootNode = rootNode;
    }
    
    protected void addParsingLogic(
        TNodeType node, Function<ParserContext<TTokenType, TNodeType>,
        ParserContext<TTokenType, TNodeType>> logic
    ) {
        rootContext.addParsingLogic(node, logic);
    }
    
    public ParserNode<TNodeType> parse() {
        return rootContext.parse(rootNode).getNode();
    }
}

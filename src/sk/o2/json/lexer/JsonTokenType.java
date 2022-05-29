package sk.o2.json.lexer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class JsonTokenType {
    private final JsonTokenTypeEnum type;
    private final Pattern pattern;
    
    public JsonTokenType(JsonTokenTypeEnum type, Pattern pattern) {
        this.type = type;
        this.pattern = pattern;
    }
    
    public boolean matches(String s) {
        return pattern.matcher(s).find();
    }
    
    public JsonToken extract(String s) {
        Matcher m = pattern.matcher(s);
        //noinspection ResultOfMethodCallIgnored
        m.find();
        return new JsonToken(type, m.group(0));
    }
}

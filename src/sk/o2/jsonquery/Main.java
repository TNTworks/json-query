package sk.o2.jsonquery;

import sk.o2.json.lexer.JsonLexer;
import sk.o2.json.parser.JsonParser;
import sk.o2.json.serializer.XmlSerializer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@SuppressWarnings("SameParameterValue")
public final class Main {
    public static void main(String[] args) throws IOException {
        String data = readResource("runtime/input.json");
        
        System.out.println(new XmlSerializer(new JsonParser(new JsonLexer(data).lex()).parse()).serialize());
    }
    
    private static String readResource(String resourcePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(resourcePath)), StandardCharsets.UTF_8);
    }
}


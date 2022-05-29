package sk.o2.json.lexer;

public enum JsonTokenEnum {
    LEFT_CURLY,
    RIGHT_CURLY,
    LEFT_SQUARE,
    RIGHT_SQUARE,
    COMMA,
    COLON,
    STRING,
    NUMBER,
    TRUE,
    FALSE,
    NULL,
    WHITESPACE,
    BAD_TOKEN,
    EOF
}

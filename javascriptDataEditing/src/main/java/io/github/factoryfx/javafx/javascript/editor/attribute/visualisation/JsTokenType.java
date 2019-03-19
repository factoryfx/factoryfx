package io.github.factoryfx.javafx.javascript.editor.attribute.visualisation;

import com.google.javascript.jscomp.parsing.parser.Keywords;
import com.google.javascript.jscomp.parsing.parser.TokenType;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;

import java.util.Optional;
import java.util.stream.Stream;

public enum JsTokenType {

    KEYWORD,
    PUNCTUATION,
    NAME,
    STRING,
    NUMBER,
    REGEX,
    ERROR,
    COMMENT
    ;

    public static Optional<JsTokenType> getByNode(Node node) {
        if (Stream.of(Keywords.values()).anyMatch(kw->kw.name().equals(node.getToken().name())))
                return Optional.of(KEYWORD);
        if (node.getToken() == Token.STRING)
            return Optional.of(STRING);
        if (node.getToken() == Token.NAME)
            return Optional.of(NAME);
        return Optional.empty();


    }

    public static Optional<JsTokenType> byTokenType(TokenType tokenType) {
        switch (tokenType) {
            case BREAK:
            case CASE:
            case CATCH:
            case CONTINUE:
            case DEBUGGER:
            case DEFAULT:
            case DELETE:
            case DO:
            case ELSE:
            case FINALLY:
            case FOR:
            case FUNCTION:
            case IF:
            case IN:
            case INSTANCEOF:
            case NEW:
            case RETURN:
            case SWITCH:
            case THIS:
            case THROW:
            case TRY:
            case TYPEOF:
            case VAR:
            case VOID:
            case WHILE:
            case WITH:
            case CLASS:
            case CONST:
            case ENUM:
            case EXPORT:
            case EXTENDS:
            case IMPORT:
            case SUPER:
            case IMPLEMENTS:
            case INTERFACE:
            case LET:
            case PACKAGE:
            case PRIVATE:
            case PROTECTED:
            case PUBLIC:
            case STATIC:
            case NULL:
            case TRUE:
            case FALSE:
                return Optional.of(JsTokenType.KEYWORD);
            case OPEN_CURLY:
            case CLOSE_CURLY:
            case OPEN_PAREN:
            case CLOSE_PAREN:
            case OPEN_SQUARE:
            case CLOSE_SQUARE:
            case PERIOD:
            case SEMI_COLON:
            case COMMA:
            case OPEN_ANGLE:
            case CLOSE_ANGLE:
            case LESS_EQUAL:
            case GREATER_EQUAL:
            case ARROW:
            case EQUAL_EQUAL:
            case NOT_EQUAL:
            case EQUAL_EQUAL_EQUAL:
            case NOT_EQUAL_EQUAL:
            case PLUS:
            case MINUS:
            case STAR:
            case STAR_STAR:
            case PERCENT:
            case PLUS_PLUS:
            case MINUS_MINUS:
            case LEFT_SHIFT:
            case RIGHT_SHIFT:
            case UNSIGNED_RIGHT_SHIFT:
            case AMPERSAND:
            case BAR:
            case CARET:
            case BANG:
            case TILDE:
            case AND:
            case OR:
            case QUESTION:
            case COLON:
            case EQUAL:
            case PLUS_EQUAL:
            case MINUS_EQUAL:
            case STAR_EQUAL:
            case STAR_STAR_EQUAL:
            case PERCENT_EQUAL:
            case LEFT_SHIFT_EQUAL:
            case RIGHT_SHIFT_EQUAL:
            case UNSIGNED_RIGHT_SHIFT_EQUAL:
            case AMPERSAND_EQUAL:
            case BAR_EQUAL:
            case CARET_EQUAL:
            case SLASH:
            case SLASH_EQUAL:
            case POUND:
                return Optional.of(JsTokenType.PUNCTUATION);
            case NUMBER:
                return Optional.of(JsTokenType.NUMBER);
            case STRING:
                return Optional.of(JsTokenType.STRING);
            case REGULAR_EXPRESSION:
                return Optional.of(JsTokenType.REGEX);
            case IDENTIFIER:
                return Optional.of(JsTokenType.NAME);
            case ERROR:
                return Optional.of(JsTokenType.ERROR);
        }
        return Optional.empty();
    }
}

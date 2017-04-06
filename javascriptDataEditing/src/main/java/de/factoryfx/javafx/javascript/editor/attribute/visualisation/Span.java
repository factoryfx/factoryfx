package de.factoryfx.javafx.javascript.editor.attribute.visualisation;

public class Span {

    final int from;
    final int len;
    final JsTokenType tokenType;

    Span(int from, int len, JsTokenType tokenType) {
        this.from = from;
        this.len = len;
        this.tokenType = tokenType;
    }

    public String style() {
        return tokenType.name();
    }
}
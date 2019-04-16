package io.github.factoryfx.docu.mock;

public class Printer {
    private final String text;

    public Printer(String text) {
        this.text = text;
    }

    public String print() {
        return text;
    }
}

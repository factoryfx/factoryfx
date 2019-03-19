package io.github.factoryfx.docu.helloworld;

public class Printer {
    private final String text;

    public Printer(String text) {
        this.text = text;
    }

    public void print() {
        System.out.println(text);
    }
}

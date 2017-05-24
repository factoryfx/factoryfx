package de.factoryfx.docu.polymorphism;

public class ErrorPrinter implements Printer {
    @Override
    public void print(String text) {
        System.err.println(text);
    }
}

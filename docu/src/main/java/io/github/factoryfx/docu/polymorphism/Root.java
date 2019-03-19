package io.github.factoryfx.docu.polymorphism;


public class Root {
    private final Printer printer;
    public Root(Printer printer) {
        this.printer=printer;
        printer.print("Hi");
    }
}

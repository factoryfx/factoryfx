package de.factoryfx.docu.polymorphism;

import de.factoryfx.docu.dependency.Dependency;

public class Root {
    private final Printer printer;
    public Root(Printer printer) {
        this.printer=printer;
        printer.print("Hi");
    }
}

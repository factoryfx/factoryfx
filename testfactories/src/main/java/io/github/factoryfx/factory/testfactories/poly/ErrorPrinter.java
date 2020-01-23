package io.github.factoryfx.factory.testfactories.poly;

public class ErrorPrinter implements Printer{
    @Override
    public void print() {
        System.err.println();
    }
}

package io.github.factoryfx.docu.datainjection;

public class Root {
    private final String text;
    private final Printer printer;

    /**
     *
     * @param text data, this is considered data that injected from the framework
     * @param printer dependency,  this is considered a dependency that injected from the framework
     */
    public Root(String text, Printer printer) {
        this.text = text;
        this.printer= printer;
    }

    public void print(){
        printer.print(text);
    }
}

package de.factoryfx.docu.parametrized;

public class Printer {

    private final String requestText;
    private final String factoryText;

    public Printer(String requestText, String factoryText) {
        this.requestText = requestText;
        this.factoryText = factoryText;
    }

    public void print() {
        System.out.println(requestText+"::"+factoryText);
    }
}

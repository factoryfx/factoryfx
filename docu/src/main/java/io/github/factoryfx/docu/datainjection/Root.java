package io.github.factoryfx.docu.datainjection;

public class Root {
    private final String text;

    public Root(String text) {
        this.text = text;
    }

    public void doX(){
        System.out.println(text);
    }
}

package io.github.factoryfx.docu.update;

public class Root {
    private String dummy;

    public Root(String dummy) {
        this.dummy=dummy;
    }

    public void setDummy(String dummy){
        this.dummy = dummy;
    }

    public void printDummy() {
        System.out.println(dummy);
    }
}

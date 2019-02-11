package de.factoryfx.docu.runtimestatus;

public class Root {
    private final String dummy;
    private final int counter;

    public Root(String dummy) {
        this.dummy = dummy;
        this.counter=0;
    }

    public Root(String dummy, int counter) {
        this.dummy = dummy;
        this.counter=counter;
    }

    public int getCounter() {
        return counter;
    }
}

package de.factoryfx.factory;

public class LoopProtector {

    boolean entered = false;
    public void enter() {
        if (entered)
            throw new IllegalStateException("Factories contains a cycle, circular dependencies are not supported cause it indicates a design flaw.");
        entered = true;
    }


    public void exit() {
        entered = false;
    }
}

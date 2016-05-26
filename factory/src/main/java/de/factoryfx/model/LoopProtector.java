package de.factoryfx.model;

public class LoopProtector {

    boolean entered = false;
    public void enter() {
        if (entered)
            throw new IllegalStateException("Factories contains cycle, circular dependencies are not supported cause it indicates a design flaw. (Workaround could be wrapping the constructor parameter with a Supplier)");
        entered = true;
    }


    public void exit() {
        entered = false;
    }
}

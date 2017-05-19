package de.factoryfx.docu.reuse;

public class ExpensiveResource {
    public ExpensiveResource(){
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

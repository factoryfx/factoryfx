package de.factoryfx.docu.reuse;

public class Root {
    private final ExpensiveResource expensiveResource;
    public Root(ExpensiveResource expensiveResource) {
        this.expensiveResource = expensiveResource;
    }

    public ExpensiveResource getExpensiveResource() {
        return expensiveResource;
    }
}

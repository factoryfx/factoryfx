package de.factoryfx.docu.reuse;

public class Root {
//    private final ExpensiveResource expensiveResource;
    public Root(ExpensiveResource expensiveResource) {
//        ExpensiveResource expensiveResource = new ExpensiveResource();
    }

    public void start() {
        System.out.println("start");

    }

    public void destroy() {
        System.out.println("destroy");
    }
}

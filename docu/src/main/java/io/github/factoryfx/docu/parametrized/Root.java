package io.github.factoryfx.docu.parametrized;


import io.github.factoryfx.factory.parametrized.ParametrizedObjectCreator;

public class Root {
    private final ParametrizedObjectCreator<PrinterCreateParameter, Printer> creator;

    public Root(ParametrizedObjectCreator<PrinterCreateParameter, Printer> creator) {
        this.creator=creator;
    }

    public void processRequest(String requestText){
        creator.create(new PrinterCreateParameter(requestText)).print();
    }
}

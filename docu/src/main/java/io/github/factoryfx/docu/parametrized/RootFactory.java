package io.github.factoryfx.docu.parametrized;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.parametrized.ParametrizedObjectCreatorAttribute;

public class RootFactory extends SimpleFactoryBase<Root, RootFactory> {
    public final ParametrizedObjectCreatorAttribute<PrinterCreateParameter,Printer,PrinterCreatorFactory> printerCreator =new ParametrizedObjectCreatorAttribute<>(PrinterCreatorFactory.class);

    @Override
    public Root createImpl() {
        return new Root(printerCreator.instance());
    }

    public RootFactory() {
        configLifeCycle().setStarter(root -> root.processRequest("123"));
    }
}

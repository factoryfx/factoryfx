package de.factoryfx.docu.parametrized;

import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.parametrized.ParametrizedObjectCreatorAttribute;

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

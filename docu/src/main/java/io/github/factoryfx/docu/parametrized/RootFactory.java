package io.github.factoryfx.docu.parametrized;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.parametrized.ParametrizedObjectCreatorAttribute;

public class RootFactory extends SimpleFactoryBase<Root, RootFactory> {
    public final ParametrizedObjectCreatorAttribute<RootFactory,PrinterCreateParameter,Printer,PrinterCreatorFactory> printerCreator =new ParametrizedObjectCreatorAttribute<>();

    @Override
    protected Root createImpl() {
        return new Root(printerCreator.instance());
    }

    public RootFactory() {
        configLifeCycle().setStarter(root -> root.processRequest("123"));
    }
}

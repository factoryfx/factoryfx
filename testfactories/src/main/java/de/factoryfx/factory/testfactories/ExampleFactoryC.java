package de.factoryfx.factory.testfactories;

import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.SimpleFactoryBase;

public class ExampleFactoryC extends SimpleFactoryBase<ExampleLiveObjectC,Void> {
    public final StringAttribute stringAttribute= new StringAttribute().labelText("ExampleB1");

    @Override
    public ExampleLiveObjectC createImpl() {
        return new ExampleLiveObjectC();
    }

}

package de.factoryfx.process;

import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.atrribute.FactoryReferenceListAttribute;

public class SubscriptionInitializerFactory extends SimpleFactoryBase<SubscriptionInitializer,Void> {
    public final StringAttribute stringAttribute= new StringAttribute().labelText("ExampleA1");

    public final FactoryReferenceAttribute<ProcessExecutor<SubscriptionProcess,SubscriptionParameter>,ProcessExecutorFactory<SubscriptionProcess,SubscriptionParameter,Void>> processExecutor= new FactoryReferenceAttribute<>();

    @Override
    public SubscriptionInitializer createImpl() {
        return new SubscriptionInitializer(processExecutor.instance());
    }
}

package de.factoryfx.process;

import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.atrribute.FactoryReferenceListAttribute;

public class ExampleFactoryA extends SimpleFactoryBase<ExampleLiveObjectA,Void> {
    public final StringAttribute stringAttribute= new StringAttribute().labelText("ExampleA1");

    public final FactoryReferenceAttribute<ProcessExecutor<ExampleProcess,ExampleProcessParameter>,ProcessExecutorFactory<ExampleProcess,ExampleProcessParameter,Void>> processExecutor= new FactoryReferenceAttribute<>();

    @Override
    public ExampleLiveObjectA createImpl() {
        return new ExampleLiveObjectA(processExecutor.instance());
    }
}

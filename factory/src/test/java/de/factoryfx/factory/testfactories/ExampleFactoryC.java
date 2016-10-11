package de.factoryfx.factory.testfactories;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.LiveCycleController;

public class ExampleFactoryC extends FactoryBase<ExampleLiveObjectC,Void> {
    public final StringAttribute stringAttribute= new StringAttribute(new AttributeMetadata().labelText("ExampleB1"));

    @Override
    public LiveCycleController<ExampleLiveObjectC, Void> createLifecycleController() {
        return new LiveCycleController<ExampleLiveObjectC, Void>() {
            @Override
            public ExampleLiveObjectC create() {
                return new ExampleLiveObjectC();
            }
        };
    }
}

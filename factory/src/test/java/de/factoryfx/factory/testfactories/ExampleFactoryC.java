package de.factoryfx.factory.testfactories;

import java.util.Optional;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.LifecycleNotifier;

public class ExampleFactoryC extends FactoryBase<ExampleLiveObjectC,Void> {
    public final StringAttribute stringAttribute= new StringAttribute(new AttributeMetadata().labelText("ExampleB1"));

    @Override
    protected ExampleLiveObjectC createImp(Optional<ExampleLiveObjectC> previousLiveObject, LifecycleNotifier<Void> lifecycle) {
        return new ExampleLiveObjectC();
    }
}

package de.factoryfx.factory.testfactories;

import java.util.Optional;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.util.StringAttribute;
import de.factoryfx.factory.FactoryBase;

public class ExampleFactoryC extends FactoryBase<ExampleLiveObjectC> {
    public final StringAttribute stringAttribute= new StringAttribute(new AttributeMetadata().labelText("ExampleB1"));

    @Override
    protected ExampleLiveObjectC createImp(Optional<ExampleLiveObjectC> previousLiveObject) {
        return new ExampleLiveObjectC();
    }
}

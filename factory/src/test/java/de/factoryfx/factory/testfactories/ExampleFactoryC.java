package de.factoryfx.factory.testfactories;

import java.util.Optional;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.attribute.StringAttribute;
import de.factoryfx.factory.attribute.builder.AttributeBuilder;

public class ExampleFactoryC extends FactoryBase<ExampleLiveObjectC,ExampleFactoryC> {
    public final StringAttribute stringAttribute= AttributeBuilder.string().labelText("ExampleB1").build();


    @Override
    protected ExampleLiveObjectC createImp(Optional<ExampleLiveObjectC> previousLiveObject) {
        return new ExampleLiveObjectC();
    }
}

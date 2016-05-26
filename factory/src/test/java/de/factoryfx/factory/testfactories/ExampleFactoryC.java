package de.factoryfx.factory.testfactories;

import java.util.Optional;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.PreviousLiveObjectProvider;
import de.factoryfx.factory.attribute.AttributeMetadata;
import de.factoryfx.factory.attribute.StringAttribute;

public class ExampleFactoryC extends FactoryBase<ExampleLiveObjectC,ExampleFactoryC> {
    public StringAttribute stringAttribute=new StringAttribute(new AttributeMetadata<>("ExampleB1"));


    @Override
    protected ExampleLiveObjectC createImp(Optional<ExampleLiveObjectC> previousLiveObject, PreviousLiveObjectProvider previousLiveObjectProvider) {
        return new ExampleLiveObjectC();
    }
}

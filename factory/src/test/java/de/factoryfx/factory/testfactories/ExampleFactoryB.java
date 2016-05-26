package de.factoryfx.factory.testfactories;

import java.util.Optional;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.PreviousLiveObjectProvider;
import de.factoryfx.factory.attribute.AttributeMetadata;
import de.factoryfx.factory.attribute.ReferenceAttribute;
import de.factoryfx.factory.attribute.StringAttribute;

public class ExampleFactoryB extends FactoryBase<ExampleLiveObjectB,ExampleFactoryB> {
    public StringAttribute stringAttribute=new StringAttribute(new AttributeMetadata<>("ExampleB1"));
    public ReferenceAttribute<ExampleFactoryA> referenceAttribute = new ReferenceAttribute<ExampleFactoryA>(new AttributeMetadata<>("ExampleB2"));
    public ReferenceAttribute<ExampleFactoryC> referenceAttributeC = new ReferenceAttribute<ExampleFactoryC>(new AttributeMetadata<>("ExampleB2"));

    @Override
    protected ExampleLiveObjectB createImp(Optional<ExampleLiveObjectB> previousLiveObject, PreviousLiveObjectProvider previousLiveObjectProvider) {
        return new ExampleLiveObjectB(referenceAttributeC.get().create(previousLiveObjectProvider));
    }
}

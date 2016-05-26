package de.factoryfx.model.testfactories;

import de.factoryfx.model.ClosedPreviousLiveObject;
import de.factoryfx.model.FactoryBase;
import de.factoryfx.model.attribute.AttributeMetadata;
import de.factoryfx.model.attribute.ReferenceAttribute;
import de.factoryfx.model.attribute.StringAttribute;

public class ExampleFactoryB extends FactoryBase<ExampleLiveObjectB,ExampleFactoryB> {
    public StringAttribute stringAttribute=new StringAttribute(new AttributeMetadata<>("ExampleB1"));
    public ReferenceAttribute<ExampleFactoryA> referenceAttribute = new ReferenceAttribute<ExampleFactoryA>(new AttributeMetadata<>("ExampleB2"));
    public ReferenceAttribute<ExampleFactoryC> referenceAttributeC = new ReferenceAttribute<ExampleFactoryC>(new AttributeMetadata<>("ExampleB2"));

    @Override
    protected ExampleLiveObjectB createImp(ClosedPreviousLiveObject<ExampleLiveObjectB> closedPreviousLiveObject) {
        return new ExampleLiveObjectB(referenceAttribute.get().create(null));
    }
}

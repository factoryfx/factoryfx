package de.factoryfx.factory.testfactories;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;

public class ExampleFactoryB extends SimpleFactoryBase<ExampleLiveObjectB,Void> {
    public final StringAttribute stringAttribute= new StringAttribute(new AttributeMetadata().labelText("ExampleB1"));
    public final FactoryReferenceAttribute<ExampleLiveObjectA,ExampleFactoryA> referenceAttribute = new FactoryReferenceAttribute<>(ExampleFactoryA.class,new AttributeMetadata().labelText("ExampleB2"));
    public final FactoryReferenceAttribute<ExampleLiveObjectC,ExampleFactoryC> referenceAttributeC = new FactoryReferenceAttribute<>(ExampleFactoryC.class,new AttributeMetadata().labelText("ExampleC2"));

    @Override
    public ExampleLiveObjectB createImpl() {
        return new ExampleLiveObjectB(referenceAttributeC.instance());
    }

}

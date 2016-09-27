package de.factoryfx.factory.testfactories;

import java.util.Optional;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;

public class ExampleFactoryB extends FactoryBase<ExampleLiveObjectB> {
    public final StringAttribute stringAttribute= new StringAttribute(new AttributeMetadata().labelText("ExampleB1"));
    public final FactoryReferenceAttribute<ExampleLiveObjectA,ExampleFactoryA> referenceAttribute = new FactoryReferenceAttribute<>(ExampleFactoryA.class,new AttributeMetadata().labelText("ExampleB2"));
    public final FactoryReferenceAttribute<ExampleLiveObjectC,ExampleFactoryC> referenceAttributeC = new FactoryReferenceAttribute<>(ExampleFactoryC.class,new AttributeMetadata().labelText("ExampleC2"));

    @Override
    protected ExampleLiveObjectB createImp(Optional<ExampleLiveObjectB> previousLiveObject) {
        return new ExampleLiveObjectB(referenceAttributeC.instance());
    }
}

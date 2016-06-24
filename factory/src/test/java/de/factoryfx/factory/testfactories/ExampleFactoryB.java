package de.factoryfx.factory.testfactories;

import java.util.Optional;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.attribute.AttributeMetadata;
import de.factoryfx.factory.attribute.ReferenceAttribute;
import de.factoryfx.factory.attribute.StringAttribute;

public class ExampleFactoryB extends FactoryBase<ExampleLiveObjectB,ExampleFactoryB> {
    public final StringAttribute stringAttribute= new StringAttribute(new AttributeMetadata().labelText("ExampleB1"));
    public final ReferenceAttribute<ExampleFactoryA> referenceAttribute = new ReferenceAttribute<ExampleFactoryA>(new AttributeMetadata().labelText("ExampleB2"));
    public final ReferenceAttribute<ExampleFactoryC> referenceAttributeC = new ReferenceAttribute<ExampleFactoryC>(new AttributeMetadata().labelText("ExampleC2"));

    @Override
    protected ExampleLiveObjectB createImp(Optional<ExampleLiveObjectB> previousLiveObject) {
        return new ExampleLiveObjectB(referenceAttributeC.get().create());
    }
}

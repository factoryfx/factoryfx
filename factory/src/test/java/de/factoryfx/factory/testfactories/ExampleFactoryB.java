package de.factoryfx.factory.testfactories;

import java.util.Optional;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.attribute.ReferenceAttribute;
import de.factoryfx.factory.attribute.StringAttribute;
import de.factoryfx.factory.attribute.builder.AttributeBuilder;

public class ExampleFactoryB extends FactoryBase<ExampleLiveObjectB,ExampleFactoryB> {
    public final StringAttribute stringAttribute= AttributeBuilder.string().labelText("ExampleB1").build();
    public final ReferenceAttribute<ExampleFactoryA> referenceAttribute = AttributeBuilder.<ExampleFactoryA>reference().labelText("ExampleB2").build();
    public final ReferenceAttribute<ExampleFactoryC> referenceAttributeC = AttributeBuilder.<ExampleFactoryC>reference().labelText("ExampleC2").build();

    @Override
    protected ExampleLiveObjectB createImp(Optional<ExampleLiveObjectB> previousLiveObject) {
        return new ExampleLiveObjectB(referenceAttributeC.get().create());
    }
}

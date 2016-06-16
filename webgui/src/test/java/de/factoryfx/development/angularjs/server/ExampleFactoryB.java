package de.factoryfx.development.angularjs.server;

import java.util.Optional;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.attribute.ReferenceAttribute;
import de.factoryfx.factory.attribute.StringAttribute;
import de.factoryfx.factory.attribute.builder.AttributeBuilder;

public class ExampleFactoryB extends FactoryBase<ExampleLiveObjectB,ExampleFactoryB> {
    public final StringAttribute stringAttribute= AttributeBuilder.string().en("ExampleB1en").de("ExampleB2de").build();
    public ReferenceAttribute<ExampleFactoryA> referenceAttribute=AttributeBuilder.<ExampleFactoryA>reference().en("ExampleB2en").de("ExampleB2de").build();

    @Override
    protected ExampleLiveObjectB createImp(Optional<ExampleLiveObjectB> previousLiveObject) {
        return new ExampleLiveObjectB();
    }
}

package de.factoryfx.development.angularjs.server;

import java.util.Optional;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.attribute.AttributeMetadata;
import de.factoryfx.factory.attribute.ReferenceAttribute;
import de.factoryfx.factory.attribute.StringAttribute;

public class ExampleFactoryB extends FactoryBase<ExampleLiveObjectB,ExampleFactoryB> {
    public final StringAttribute stringAttribute= new StringAttribute(new AttributeMetadata().en("ExampleB1en").de("ExampleB2de"));
    public ReferenceAttribute<ExampleFactoryA> referenceAttribute=new ReferenceAttribute<>(ExampleFactoryA.class,new AttributeMetadata().en("ExampleB2en").de("ExampleB2de"));

    @Override
    protected ExampleLiveObjectB createImp(Optional<ExampleLiveObjectB> previousLiveObject) {
        return new ExampleLiveObjectB();
    }
}

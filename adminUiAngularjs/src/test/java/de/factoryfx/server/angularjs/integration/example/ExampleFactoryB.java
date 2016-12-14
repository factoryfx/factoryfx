package de.factoryfx.server.angularjs.integration.example;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;

public class ExampleFactoryB extends SimpleFactoryBase<ExampleLiveObjectB,ExampleVisitor> {
    public final StringAttribute stringAttribute= new StringAttribute(new AttributeMetadata().en("ExampleB1en").de("ExampleB2de"));
    public FactoryReferenceAttribute<ExampleLiveObjectA,ExampleFactoryA> referenceAttribute=new FactoryReferenceAttribute<>(ExampleFactoryA.class,new AttributeMetadata().en("ExampleB2en").de("ExampleB2de"));

    @Override
    public ExampleLiveObjectB createImpl() {
        return new ExampleLiveObjectB();
    }

}

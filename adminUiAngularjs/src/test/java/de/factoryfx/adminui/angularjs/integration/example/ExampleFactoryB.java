package de.factoryfx.adminui.angularjs.integration.example;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.LiveCycleController;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;

public class ExampleFactoryB extends FactoryBase<ExampleLiveObjectB,ExampleVisitor> {
    public final StringAttribute stringAttribute= new StringAttribute(new AttributeMetadata().en("ExampleB1en").de("ExampleB2de"));
    public FactoryReferenceAttribute<ExampleLiveObjectA,ExampleFactoryA> referenceAttribute=new FactoryReferenceAttribute<>(ExampleFactoryA.class,new AttributeMetadata().en("ExampleB2en").de("ExampleB2de"));


    @Override
    public LiveCycleController<ExampleLiveObjectB, ExampleVisitor> createLifecycleController() {
        return () -> {
            return new ExampleLiveObjectB();
        };
    }
}

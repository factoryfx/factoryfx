package de.factoryfx.factory.testfactories;

import java.util.ArrayList;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.LiveCycleController;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.atrribute.FactoryReferenceListAttribute;

public class ExampleFactoryA extends FactoryBase<ExampleLiveObjectA,Void> {
    public final StringAttribute stringAttribute= new StringAttribute(new AttributeMetadata().labelText("ExampleA1"));
    public final FactoryReferenceAttribute<ExampleLiveObjectB,ExampleFactoryB> referenceAttribute = new FactoryReferenceAttribute<>(ExampleFactoryB.class,new AttributeMetadata().labelText("ExampleA2"));
    public final FactoryReferenceListAttribute<ExampleLiveObjectB,ExampleFactoryB> referenceListAttribute = new FactoryReferenceListAttribute<>(ExampleFactoryB.class,new AttributeMetadata().labelText("ExampleA3"));


    @Override
    public LiveCycleController<ExampleLiveObjectA, Void> createLifecycleController() {
        return () -> {
            ArrayList<ExampleLiveObjectB> exampleLiveObjectBs = new ArrayList<>();
            referenceListAttribute.get().forEach(exampleFactoryB -> {
                exampleLiveObjectBs.add(exampleFactoryB.instance());
            });

            return new ExampleLiveObjectA(referenceAttribute.instance(), exampleLiveObjectBs);
        };
    }
}

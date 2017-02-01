package de.factoryfx.factory.testfactories;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.atrribute.FactoryReferenceListAttribute;

public class ExampleFactoryA extends SimpleFactoryBase<ExampleLiveObjectA,Void> {
    public final StringAttribute stringAttribute= new StringAttribute(new AttributeMetadata().labelText("ExampleA1"));
    public final FactoryReferenceAttribute<ExampleLiveObjectB,ExampleFactoryB> referenceAttribute = new FactoryReferenceAttribute<>(ExampleFactoryB.class,new AttributeMetadata().labelText("ExampleA2"));
    public final FactoryReferenceListAttribute<ExampleLiveObjectB,ExampleFactoryB> referenceListAttribute = new FactoryReferenceListAttribute<>(ExampleFactoryB.class,new AttributeMetadata().labelText("ExampleA3"));

    @Override
    public ExampleLiveObjectA createImpl() {
        return new ExampleLiveObjectA(referenceAttribute.instance(), referenceListAttribute.instances());
    }

}
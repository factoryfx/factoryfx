package de.factoryfx.model.testfactories;

import java.util.ArrayList;

import de.factoryfx.model.ClosedPreviousLiveObject;
import de.factoryfx.model.FactoryBase;
import de.factoryfx.model.attribute.AttributeMetadata;
import de.factoryfx.model.attribute.ListMetadata;
import de.factoryfx.model.attribute.ReferenceAttribute;
import de.factoryfx.model.attribute.ReferenceListAttribute;
import de.factoryfx.model.attribute.StringAttribute;

public class ExampleFactoryA extends FactoryBase<ExampleLiveObjectA,ExampleFactoryA> {
    public StringAttribute stringAttribute=new StringAttribute(new AttributeMetadata<>("ExampleA1"));
    public ReferenceAttribute<ExampleFactoryB> referenceAttribute = new ReferenceAttribute<ExampleFactoryB>(new AttributeMetadata<>("ExampleA2"));
    public ReferenceListAttribute<ExampleFactoryB> referenceListAttribute = new ReferenceListAttribute<ExampleFactoryB>(new ListMetadata<>("ExampleA3"));

    @Override
    protected ExampleLiveObjectA createImp(ClosedPreviousLiveObject<ExampleLiveObjectA> closedPreviousLiveObject) {
        ArrayList<ExampleLiveObjectB> exampleLiveObjectBs = new ArrayList<>();
        referenceListAttribute.get().forEach(exampleFactoryB -> {
            exampleLiveObjectBs.add(exampleFactoryB.create(null));
        });

        return new ExampleLiveObjectA(referenceAttribute.get().create(null), exampleLiveObjectBs);
    }
}

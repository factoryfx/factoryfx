package de.factoryfx.development.angularjs.server;

import java.util.ArrayList;
import java.util.Optional;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.attribute.AttributeMetadata;
import de.factoryfx.factory.attribute.ListMetadata;
import de.factoryfx.factory.attribute.ReferenceAttribute;
import de.factoryfx.factory.attribute.ReferenceListAttribute;
import de.factoryfx.factory.attribute.StringAttribute;

public class ExampleFactoryA extends FactoryBase<ExampleLiveObjectA,ExampleFactoryA> {
    public StringAttribute stringAttribute=new StringAttribute(new AttributeMetadata<>("ExampleA1"));
    public ReferenceAttribute<ExampleFactoryB> referenceAttribute = new ReferenceAttribute<ExampleFactoryB>(new AttributeMetadata<>("ExampleA2"));
    public ReferenceListAttribute<ExampleFactoryB> referenceListAttribute = new ReferenceListAttribute<ExampleFactoryB>(new ListMetadata<>("ExampleA3"));

    @Override
    protected ExampleLiveObjectA createImp(Optional<ExampleLiveObjectA> previousLiveObject) {
        ArrayList<ExampleLiveObjectB> exampleLiveObjectBs = new ArrayList<>();
        referenceListAttribute.get().forEach(exampleFactoryB -> {
            exampleLiveObjectBs.add(exampleFactoryB.create());
        });

        return new ExampleLiveObjectA(referenceAttribute.get().create(), exampleLiveObjectBs);
    }
}

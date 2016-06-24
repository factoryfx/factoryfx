package de.factoryfx.development.angularjs.server;

import java.util.ArrayList;
import java.util.Optional;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.attribute.AttributeMetadata;
import de.factoryfx.factory.attribute.ReferenceAttribute;
import de.factoryfx.factory.attribute.ReferenceListAttribute;
import de.factoryfx.factory.attribute.StringAttribute;

public class ExampleFactoryA extends FactoryBase<ExampleLiveObjectA,ExampleFactoryA> {
    public final StringAttribute stringAttribute=new StringAttribute(new AttributeMetadata().en("ExampleA1en").de("ExampleA1ger"));
    public final ReferenceAttribute<ExampleFactoryB> referenceAttribute = new ReferenceAttribute<>(new AttributeMetadata().en("ExampleA2en").de("ExampleA2de"));
    public final ReferenceListAttribute<ExampleFactoryB> referenceListAttribute = new ReferenceListAttribute<>(new AttributeMetadata().en("ExampleA3en").de("ExampleA2de"));

    @Override
    protected ExampleLiveObjectA createImp(Optional<ExampleLiveObjectA> previousLiveObject) {
        ArrayList<ExampleLiveObjectB> exampleLiveObjectBs = new ArrayList<>();
        referenceListAttribute.get().forEach(exampleFactoryB -> {
            exampleLiveObjectBs.add(exampleFactoryB.create());
        });

        return new ExampleLiveObjectA(referenceAttribute.get().create(), exampleLiveObjectBs);
    }
}

package de.factoryfx.development.angularjs.server;

import java.util.ArrayList;
import java.util.Optional;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.attribute.ReferenceAttribute;
import de.factoryfx.factory.attribute.ReferenceListAttribute;
import de.factoryfx.factory.attribute.StringAttribute;
import de.factoryfx.factory.attribute.builder.AttributeBuilder;

public class ExampleFactoryA extends FactoryBase<ExampleLiveObjectA,ExampleFactoryA> {
    public final StringAttribute stringAttribute=AttributeBuilder.string().en("ExampleA1en").de("ExampleA1ger").build();
    public final ReferenceAttribute<ExampleFactoryB> referenceAttribute = AttributeBuilder.<ExampleFactoryB>reference().en("ExampleA2en").de("ExampleA2de").build();
    public final ReferenceListAttribute<ExampleFactoryB> referenceListAttribute = AttributeBuilder.<ExampleFactoryB>referenceList().en("ExampleA3en").de("ExampleA2de").build();

    @Override
    protected ExampleLiveObjectA createImp(Optional<ExampleLiveObjectA> previousLiveObject) {
        ArrayList<ExampleLiveObjectB> exampleLiveObjectBs = new ArrayList<>();
        referenceListAttribute.get().forEach(exampleFactoryB -> {
            exampleLiveObjectBs.add(exampleFactoryB.create());
        });

        return new ExampleLiveObjectA(referenceAttribute.get().create(), exampleLiveObjectBs);
    }
}

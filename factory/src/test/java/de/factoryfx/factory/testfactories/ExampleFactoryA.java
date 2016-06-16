package de.factoryfx.factory.testfactories;

import java.util.ArrayList;
import java.util.Optional;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.attribute.ReferenceAttribute;
import de.factoryfx.factory.attribute.ReferenceListAttribute;
import de.factoryfx.factory.attribute.StringAttribute;
import de.factoryfx.factory.attribute.builder.AttributeBuilder;

public class ExampleFactoryA extends FactoryBase<ExampleLiveObjectA,ExampleFactoryA> {
    public final StringAttribute stringAttribute= AttributeBuilder.string().labelText("ExampleA1").build();
    public final ReferenceAttribute<ExampleFactoryB> referenceAttribute = AttributeBuilder.<ExampleFactoryB>reference().labelText("ExampleA2").build();
    public final ReferenceListAttribute<ExampleFactoryB> referenceListAttribute = AttributeBuilder.<ExampleFactoryB>referenceList().labelText("ExampleA3").build();

    @Override
    protected ExampleLiveObjectA createImp(Optional<ExampleLiveObjectA> previousLiveObject) {
        ArrayList<ExampleLiveObjectB> exampleLiveObjectBs = new ArrayList<>();
        referenceListAttribute.get().forEach(exampleFactoryB -> {
            exampleLiveObjectBs.add(exampleFactoryB.create());
        });

        return new ExampleLiveObjectA(referenceAttribute.get().create(), exampleLiveObjectBs);
    }
}

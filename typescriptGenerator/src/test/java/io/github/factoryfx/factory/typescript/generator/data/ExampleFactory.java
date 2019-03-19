package io.github.factoryfx.factory.typescript.generator.data;

import io.github.factoryfx.data.attribute.types.StringAttribute;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import io.github.factoryfx.factory.atrribute.FactoryReferenceListAttribute;

public class ExampleFactory extends SimpleFactoryBase<Void,ExampleFactory> {
    public final StringAttribute attribute= new StringAttribute().en("labelEn\"\'\\").de("labelDe");
    public final FactoryReferenceAttribute<Void,ExampleFactory> ref= new FactoryReferenceAttribute<>(ExampleFactory.class);
    public final FactoryReferenceListAttribute<Void,ExampleFactory> refList= new FactoryReferenceListAttribute<>(ExampleFactory.class);

    @Override
    public Void createImpl() {
        return null;
    }
}

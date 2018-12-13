package de.factoryfx.factory.typescript.generator.data;

import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.atrribute.FactoryReferenceListAttribute;

public class ExampleFactory extends SimpleFactoryBase<Void,Void,ExampleFactory> {
    public final StringAttribute attribute= new StringAttribute().en("labelEn\"\'\\").de("labelDe");
    public final FactoryReferenceAttribute<Void,ExampleFactory> ref= new FactoryReferenceAttribute<>(ExampleFactory.class);
    public final FactoryReferenceListAttribute<Void,ExampleFactory> refList= new FactoryReferenceListAttribute<>(ExampleFactory.class);

    @Override
    public Void createImpl() {
        return null;
    }
}

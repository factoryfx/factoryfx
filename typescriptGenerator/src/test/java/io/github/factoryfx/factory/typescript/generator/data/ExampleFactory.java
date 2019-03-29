package io.github.factoryfx.factory.typescript.generator.data;

import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryReferenceAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryReferenceListAttribute;

public class ExampleFactory extends SimpleFactoryBase<Void,ExampleData> {
    public final StringAttribute attribute= new StringAttribute().en("labelEn\"\'\\").de("labelDe");
    public final FactoryReferenceAttribute<ExampleData,Void,ExampleFactory> ref= new FactoryReferenceAttribute<>();
    public final FactoryReferenceListAttribute<ExampleData,Void,ExampleFactory> refList= new FactoryReferenceListAttribute<>();

    @Override
    public Void createImpl() {
        return null;
    }
}

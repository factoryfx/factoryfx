package io.github.factoryfx.factory.typescript.generator.data;

import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryListAttribute;

public class ExampleFactory extends SimpleFactoryBase<Void,ExampleData> {
    public final StringAttribute attribute= new StringAttribute().en("labelEn\"\'\\").de("labelDe");
    public final FactoryAttribute<Void,ExampleFactory> ref= new FactoryAttribute<>();
    public final FactoryListAttribute<Void,ExampleFactory> refList= new FactoryListAttribute<>();

    @Override
    protected Void createImpl() {
        return null;
    }
}

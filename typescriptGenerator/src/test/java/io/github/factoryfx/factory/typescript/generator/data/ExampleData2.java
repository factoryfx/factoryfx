package io.github.factoryfx.factory.typescript.generator.data;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.factory.attribute.types.StringAttribute;

public class ExampleData2 extends FactoryBase<Void,ExampleData> {
    public final StringAttribute attribute= new StringAttribute();
    public final FactoryAttribute<ExampleData,Void,ExampleData3> ref= new FactoryAttribute<>();
}

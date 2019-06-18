package io.github.factoryfx.factory.typescript.generator.data;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryListAttribute;
import io.github.factoryfx.factory.attribute.types.StringAttribute;

public class ExampleData extends FactoryBase<Void,ExampleData> {
    public final StringAttribute attribute= new StringAttribute().en("labelEn\"\'\\").de("labelDe");
    public final FactoryAttribute<ExampleData,Void,ExampleData2> ref= new FactoryAttribute<ExampleData,Void,ExampleData2>().nullable();
    public final FactoryListAttribute<ExampleData,Void,ExampleData2> refList= new FactoryListAttribute<>();
}

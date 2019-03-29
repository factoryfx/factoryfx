package io.github.factoryfx.factory.typescript.generator.data;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryReferenceAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryReferenceListAttribute;
import io.github.factoryfx.factory.attribute.types.StringAttribute;

public class ExampleData extends FactoryBase<Void,ExampleData> {
    public final StringAttribute attribute= new StringAttribute().en("labelEn\"\'\\").de("labelDe");
    public final FactoryReferenceAttribute<ExampleData,Void,ExampleData2> ref= new FactoryReferenceAttribute<>();
    public final FactoryReferenceListAttribute<ExampleData,Void,ExampleData2> refList= new FactoryReferenceListAttribute<>();
}

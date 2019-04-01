package io.github.factoryfx.factory.storage.migration.metadata;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryReferenceAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryReferenceListAttribute;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.merge.testdata.ExampleDataA;
import io.github.factoryfx.factory.merge.testdata.ExampleDataB;


public class ExampleDataAPrevious extends FactoryBase<Void, ExampleDataA> {
    public final StringAttribute stringAttribute = new StringAttribute().labelText("ExampleA1");
    public final FactoryReferenceAttribute<ExampleDataA,Void,ExampleDataB> referenceAttribute = new FactoryReferenceAttribute<>();
    public final FactoryReferenceListAttribute<ExampleDataA,Void,ExampleDataB> referenceListAttribute = new FactoryReferenceListAttribute<>();
    public final StringAttribute garbage = new StringAttribute();

}

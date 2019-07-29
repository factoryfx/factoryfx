package io.github.factoryfx.factory.storage.migration.metadata;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryListAttribute;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.merge.testdata.ExampleDataA;
import io.github.factoryfx.factory.merge.testdata.ExampleDataB;


public class ExampleDataAPrevious extends FactoryBase<Void, ExampleDataA> {
    public final StringAttribute stringAttribute = new StringAttribute().labelText("ExampleA1");
    public final FactoryAttribute<Void,ExampleDataB> referenceAttribute = new FactoryAttribute<>();
    public final FactoryListAttribute<Void,ExampleDataB> referenceListAttribute = new FactoryListAttribute<>();
    public final StringAttribute garbage = new StringAttribute();

}

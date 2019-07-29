package io.github.factoryfx.factory.merge.testdata;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.factory.attribute.types.StringAttribute;


public class ExampleDataB extends FactoryBase<Void,ExampleDataA> {
    public final StringAttribute stringAttribute= new StringAttribute().labelText("ExampleB1");
    public final FactoryAttribute<Void,ExampleDataA> referenceAttribute = new FactoryAttribute<Void,ExampleDataA>().labelText("ExampleB2");
    public final FactoryAttribute<Void,ExampleDataC> referenceAttributeC = new FactoryAttribute<Void,ExampleDataC>().labelText("ExampleC2");
}

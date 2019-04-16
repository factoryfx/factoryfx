package io.github.factoryfx.factory.merge.testdata;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.types.StringAttribute;


public class ExampleDataC extends FactoryBase<Void,ExampleDataA> {
    public final StringAttribute stringAttribute = new StringAttribute().labelText("ExampleB1");

}


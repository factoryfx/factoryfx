package io.github.factoryfx.javafx.editor.data;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.types.StringAttribute;

public class ExampleData2 extends FactoryBase<Void,ExampleData1> {

    public final StringAttribute stringAttribute=new StringAttribute().en("StringAttribute").de("StringAttribute de").defaultValue("123");

    public ExampleData2(){
        config().setDisplayTextProvider(() -> stringAttribute.get());
        config().setDisplayTextDependencies(stringAttribute);
    }

}

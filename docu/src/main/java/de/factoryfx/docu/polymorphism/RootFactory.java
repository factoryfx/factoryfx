package de.factoryfx.docu.polymorphism;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;

public class RootFactory extends SimpleFactoryBase<Root,Void>{
    public final FactoryReferenceAttribute<Printer,SimpleFactoryBase<Printer,Void>> printer =new FactoryReferenceAttribute<>(new AttributeMetadata().labelText("dependency"),SimpleFactoryBase.class);

    @Override
    public Root createImpl() {
        return new Root(printer.instance());
    }
}

package de.factoryfx.docu.parametrized;

import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.parametrized.ParametrizedObjectCreatorFactory;

import java.util.function.Function;

public class PrinterCreatorFactory  extends ParametrizedObjectCreatorFactory<PrinterCreateParameter,Printer,Void> {
    public final StringAttribute text = new StringAttribute();


    @Override
    protected Function<PrinterCreateParameter, Printer> getCreator() {
        return p -> new Printer(p.text,text.get());
    }
}

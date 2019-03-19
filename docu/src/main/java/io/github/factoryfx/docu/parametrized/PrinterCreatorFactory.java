package io.github.factoryfx.docu.parametrized;

import io.github.factoryfx.data.attribute.types.StringAttribute;
import io.github.factoryfx.factory.parametrized.ParametrizedObjectCreatorFactory;

import java.util.function.Function;

public class PrinterCreatorFactory  extends ParametrizedObjectCreatorFactory<PrinterCreateParameter,Printer,PrinterCreatorFactory> {
    public final StringAttribute text = new StringAttribute();


    @Override
    protected Function<PrinterCreateParameter, Printer> getCreator() {
        return p -> new Printer(p.text,text.get());
    }
}

package io.github.factoryfx.docu.mock;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.util.LanguageText;
import io.github.factoryfx.factory.validation.ValidationResult;

public class PrinterFactory extends FactoryBase<Printer, PrinterFactory> {
    public final StringAttribute text=new StringAttribute();

    public PrinterFactory(){
        configLifeCycle().setCreator(() -> new Printer(text.get()));
        configLifeCycle().setStarter(Printer::print);
    }

}

package io.github.factoryfx.docu.helloworld;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.util.LanguageText;
import io.github.factoryfx.factory.validation.ValidationResult;
import io.github.factoryfx.factory.FactoryBase;

public class PrinterFactory extends SimpleFactoryBase<Printer, PrinterFactory> {
    public final StringAttribute text=new StringAttribute();

    @Override
    public Printer createImpl() {
        return new Printer(text.get());
    }
}

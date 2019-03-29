package io.github.factoryfx.docu.helloworld;

import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.util.LanguageText;
import io.github.factoryfx.factory.validation.ValidationResult;
import io.github.factoryfx.factory.FactoryBase;

public class PrinterFactory extends FactoryBase<Printer, PrinterFactory> {
    public final StringAttribute text=new StringAttribute().labelText("Text").validation(value -> {
        return new ValidationResult("Hello World".equals(value),new LanguageText().en("Text wrong"));
    });

    public PrinterFactory(){
        configLifeCycle().setCreator(() -> new Printer(text.get()));
        configLifeCycle().setStarter(Printer::print);
    }

}

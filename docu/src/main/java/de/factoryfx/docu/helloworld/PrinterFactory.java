package de.factoryfx.docu.helloworld;

import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.data.util.LanguageText;
import de.factoryfx.data.validation.Validation;
import de.factoryfx.data.validation.ValidationResult;
import de.factoryfx.factory.FactoryBase;

public class PrinterFactory extends FactoryBase<Printer,Void, PrinterFactory> {
    public final StringAttribute text=new StringAttribute().labelText("Text").validation(value -> {
        return new ValidationResult("Hello World".equals(value),new LanguageText().en("Text wrong"));
    });

    public PrinterFactory(){
        configLiveCycle().setCreator(() -> new Printer(text.get()));
        configLiveCycle().setStarter(Printer::print);
    }

}

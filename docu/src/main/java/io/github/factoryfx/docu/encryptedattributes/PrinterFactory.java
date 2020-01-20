package io.github.factoryfx.docu.encryptedattributes;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.types.EncryptedStringAttribute;

public class PrinterFactory extends SimpleFactoryBase<Printer, PrinterFactory> {
    public final EncryptedStringAttribute password=new EncryptedStringAttribute();

    @Override
    protected Printer createImpl() {
        return new Printer(password.get().decrypt(Main.KEY));
    }
}

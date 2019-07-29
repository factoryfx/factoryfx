package io.github.factoryfx.docu.permission;

import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.jetty.JettyServerFactory;
import org.eclipse.jetty.server.Server;

public class PrinterFactory extends FactoryBase<Printer, PrinterFactory> {
    public static final String CHANGE_TEXT_PERMISSION = "CHANGE_TEXT";

    public final StringAttribute text=new StringAttribute().permission(CHANGE_TEXT_PERMISSION);
    public final FactoryAttribute<Server, JettyServerFactory<PrinterFactory>> server = new FactoryAttribute<>();

    public PrinterFactory(){
        configLifeCycle().setCreator(() -> new Printer(text.get()));
        configLifeCycle().setStarter(Printer::print);
    }

}

package de.factoryfx.docu.permission;

import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.docu.restserver.SimpleHttpServer;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.jetty.JettyServerFactory;
import org.eclipse.jetty.server.Server;

public class PrinterFactory extends FactoryBase<Printer, PrinterFactory> {
    public static final String CHANGE_TEXT_PERMISSION = "CHANGE_TEXT";
    public final StringAttribute text=new StringAttribute().permission(CHANGE_TEXT_PERMISSION);
    @SuppressWarnings("unchecked")
    public final FactoryReferenceAttribute<Server, JettyServerFactory<SimpleHttpServer>> server = FactoryReferenceAttribute.create(new FactoryReferenceAttribute<>(JettyServerFactory.class));


    public PrinterFactory(){
        configLifeCycle().setCreator(() -> new Printer(text.get()));
        configLifeCycle().setStarter(Printer::print);
    }

}

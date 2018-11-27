package de.factoryfx.docu.permission;

import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.jetty.JettyServer;

public class PrinterFactory extends FactoryBase<Printer,Void, PrinterFactory> {
    public static final String CHANGE_TEXT_PERMISSION = "CHANGE_TEXT";
    public final StringAttribute text=new StringAttribute().permission(CHANGE_TEXT_PERMISSION);
    public final FactoryReferenceAttribute<JettyServer,PermissionJettyServerFactory> server=new FactoryReferenceAttribute<>(PermissionJettyServerFactory.class).labelText("server");

    public PrinterFactory(){
        configLifeCycle().setCreator(() -> new Printer(text.get()));
        configLifeCycle().setStarter(Printer::print);
    }

}

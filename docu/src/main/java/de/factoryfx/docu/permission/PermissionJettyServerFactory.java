package de.factoryfx.docu.permission;

import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.jetty.JettyServerFactory;
import de.factoryfx.jetty.ServletBuilder;
import de.factoryfx.microservice.rest.MicroserviceResource;

public class PermissionJettyServerFactory extends JettyServerFactory<Void, PrinterFactory> {
    public final FactoryReferenceAttribute<MicroserviceResource<Void, PrinterFactory,Void>, PrinterMicroserviceResourceFactory> resource = new FactoryReferenceAttribute<>(PrinterMicroserviceResourceFactory.class);

    @Override
    protected void setupServlets(ServletBuilder servletBuilder) {
        defaultSetupServlets(servletBuilder, resource.instance());
    }
}

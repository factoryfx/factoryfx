package de.factoryfx.servlet;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.server.rest.MicroserviceResource;
import de.factoryfx.server.rest.MicroserviceResourceFactory;

public class ApplicationServerRestServletBridgeFactory<R extends FactoryBase<?,ServletContextAwareVisitor,R>,S> extends FactoryBase<ApplicationServerRestServletBridge,ServletContextAwareVisitor,R> {

    public final FactoryReferenceAttribute<MicroserviceResource,MicroserviceResourceFactory<ServletContextAwareVisitor,R,S>> applicationServerResource = new FactoryReferenceAttribute<MicroserviceResource,MicroserviceResourceFactory<ServletContextAwareVisitor,R,S>>().setupUnsafe(MicroserviceResourceFactory.class);

    public ApplicationServerRestServletBridgeFactory() {
        configLiveCycle().setCreator(() -> new ApplicationServerRestServletBridge(applicationServerResource.instance(),null));
        configLiveCycle().setReCreator(root -> new ApplicationServerRestServletBridge(applicationServerResource.instance(),root.getUpdateableServlet()));

        configLiveCycle().setRuntimeQueryExecutor((servletContextAwareVisitor, applicationServerRestServletBridge) -> {
            if (servletContextAwareVisitor.servletContext != null) {
                applicationServerRestServletBridge.addInitialServlet(servletContextAwareVisitor.servletContext);
            }
        });


    }
}

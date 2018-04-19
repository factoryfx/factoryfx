package de.factoryfx.servlet;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.server.rest.ApplicationServerResource;
import de.factoryfx.server.rest.ApplicationServerResourceFactory;

public class ApplicationServerRestServletBridgeFactory<R extends FactoryBase<?,ServletContextAwareVisitor,R>,S> extends FactoryBase<ApplicationServerRestServletBridge,ServletContextAwareVisitor,R> {

    public final FactoryReferenceAttribute<ApplicationServerResource,ApplicationServerResourceFactory<ServletContextAwareVisitor,R,S>> applicationServerResource = new FactoryReferenceAttribute<ApplicationServerResource,ApplicationServerResourceFactory<ServletContextAwareVisitor,R,S>>().setupUnsafe(ApplicationServerResourceFactory.class);

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

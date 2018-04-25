package de.factoryfx.servlet;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.server.rest.MicroserviceResource;
import de.factoryfx.server.rest.MicroserviceResourceFactory;

public class MicroserviceRestServletBridgeFactory<R extends FactoryBase<?,ServletContextAwareVisitor,R>,S> extends FactoryBase<MicroserviceRestServletBridge,ServletContextAwareVisitor,R> {

    public final FactoryReferenceAttribute<MicroserviceResource,MicroserviceResourceFactory<ServletContextAwareVisitor,R,S>> microserviceResource = new FactoryReferenceAttribute<MicroserviceResource,MicroserviceResourceFactory<ServletContextAwareVisitor,R,S>>().setupUnsafe(MicroserviceResourceFactory.class);

    public MicroserviceRestServletBridgeFactory() {
        configLiveCycle().setCreator(() -> new MicroserviceRestServletBridge(microserviceResource.instance(),null));
        configLiveCycle().setReCreator(root -> new MicroserviceRestServletBridge(microserviceResource.instance(),root.getUpdateableServlet()));

        configLiveCycle().setRuntimeQueryExecutor((servletContextAwareVisitor, microserviceRestServletBridge) -> {
            if (servletContextAwareVisitor.servletContext != null) {
                microserviceRestServletBridge.addInitialServlet(servletContextAwareVisitor.servletContext);
            }
        });


    }
}

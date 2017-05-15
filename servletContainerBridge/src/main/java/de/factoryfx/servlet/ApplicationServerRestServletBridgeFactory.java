package de.factoryfx.servlet;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.server.rest.ApplicationServerResource;
import de.factoryfx.server.rest.ApplicationServerResourceFactory;

import java.util.function.BiConsumer;

public class ApplicationServerRestServletBridgeFactory<RL,R extends FactoryBase<RL,ServletContextAwareVisitor>> extends FactoryBase<ApplicationServerRestServletBridge,ServletContextAwareVisitor> {

    public final FactoryReferenceAttribute<ApplicationServerResource,ApplicationServerResourceFactory<ServletContextAwareVisitor,RL,R>> applicationServerResource = new FactoryReferenceAttribute<>(new AttributeMetadata(),ApplicationServerResourceFactory.class);

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

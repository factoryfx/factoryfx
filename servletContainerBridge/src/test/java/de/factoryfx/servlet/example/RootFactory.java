package de.factoryfx.servlet.example;

import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.server.rest.ApplicationServerResourceFactory;
import de.factoryfx.servlet.ApplicationServerRestServletBridge;
import de.factoryfx.servlet.ApplicationServerRestServletBridgeFactory;
import de.factoryfx.servlet.ServletContextAwareVisitor;

public class RootFactory extends SimpleFactoryBase<Root,ServletContextAwareVisitor> {
    public final StringAttribute stringAttribute =new StringAttribute();
    public final FactoryReferenceAttribute<ApplicationServerRestServletBridge,ApplicationServerRestServletBridgeFactory<Root,RootFactory>> applicationServerRestBridge = new FactoryReferenceAttribute<ApplicationServerRestServletBridge,ApplicationServerRestServletBridgeFactory<Root,RootFactory>>().setupUnsafe(ApplicationServerResourceFactory.class);


    @Override
    public Root createImpl() {
        return new Root();
    }
}

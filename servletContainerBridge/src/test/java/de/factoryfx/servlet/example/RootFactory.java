package de.factoryfx.servlet.example;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.server.rest.ApplicationServerResource;
import de.factoryfx.server.rest.ApplicationServerResourceFactory;
import de.factoryfx.servlet.ApplicationServerRestServletBridge;
import de.factoryfx.servlet.ApplicationServerRestServletBridgeFactory;
import de.factoryfx.servlet.ServletContextAwareVisitor;

import java.util.function.BiConsumer;

public class RootFactory extends SimpleFactoryBase<Root,ServletContextAwareVisitor> {
    public final StringAttribute stringAttribute =new StringAttribute(new AttributeMetadata());
    public final FactoryReferenceAttribute<ApplicationServerRestServletBridge,ApplicationServerRestServletBridgeFactory<Root,RootFactory>> applicationServerRestBridge = new FactoryReferenceAttribute<>(new AttributeMetadata(),ApplicationServerResourceFactory.class);


    @Override
    public Root createImpl() {
        return new Root(null);
    }
}

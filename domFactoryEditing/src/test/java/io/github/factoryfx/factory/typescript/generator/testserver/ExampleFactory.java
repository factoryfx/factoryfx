package io.github.factoryfx.factory.typescript.generator.testserver;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryViewAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryViewListAttribute;
import io.github.factoryfx.factory.attribute.primitive.*;
import io.github.factoryfx.factory.attribute.time.*;
import io.github.factoryfx.factory.attribute.types.*;
import io.github.factoryfx.jetty.JettyServerFactory;
import org.eclipse.jetty.server.Server;

import java.util.List;

public class ExampleFactory extends SimpleFactoryBase<Void, TestServerFactory> {
    public final StringAttribute stringAttribute=new StringAttribute().nullable();

    @Override
    protected Void createImpl() {
        return null;
    }
}
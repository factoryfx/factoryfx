package io.github.factoryfx.factory.typescript.generator.testserver;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;

public class TestHandlerFactory extends SimpleFactoryBase<Handler, TestServerFactory> {
    public StringAttribute test = new StringAttribute();

    @Override
    protected Handler createImpl() {
        GzipHandler gzipHandler = new GzipHandler();
        return gzipHandler;
    }
}

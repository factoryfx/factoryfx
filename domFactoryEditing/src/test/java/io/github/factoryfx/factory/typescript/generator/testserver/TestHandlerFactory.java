package io.github.factoryfx.factory.typescript.generator.testserver;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import org.eclipse.jetty.compression.gzip.GzipCompression;
import org.eclipse.jetty.compression.server.CompressionHandler;
import org.eclipse.jetty.server.Handler;

public class TestHandlerFactory extends SimpleFactoryBase<Handler, TestServerFactory> {
    public StringAttribute test = new StringAttribute();

    @Override
    protected Handler createImpl() {
        CompressionHandler gzipHandler = new CompressionHandler();
        gzipHandler.putCompression(new GzipCompression());
        return gzipHandler;
    }
}

package io.github.factoryfx.jetty;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryPolymorphicAttribute;
import org.eclipse.jetty.compression.gzip.GzipCompression;
import org.eclipse.jetty.compression.server.CompressionHandler;
import org.eclipse.jetty.server.Handler;

public class GzipHandlerFactory<R extends FactoryBase<?,R>> extends SimpleFactoryBase<Handler,R> {
    public final FactoryPolymorphicAttribute<Handler> handler = new FactoryPolymorphicAttribute<Handler>().labelText("Handler");


    @Override
    protected Handler createImpl() {
        CompressionHandler gzipHandler = new CompressionHandler(handler.instance());
        gzipHandler.putCompression(new GzipCompression());
        return gzipHandler;
    }
}

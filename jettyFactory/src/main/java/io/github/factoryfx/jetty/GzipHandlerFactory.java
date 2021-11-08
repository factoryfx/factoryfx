package io.github.factoryfx.jetty;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryPolymorphicAttribute;
import io.github.factoryfx.factory.attribute.primitive.BooleanAttribute;
import io.github.factoryfx.factory.attribute.primitive.IntegerAttribute;
import io.github.factoryfx.factory.attribute.types.EnumListAttribute;
import io.github.factoryfx.factory.attribute.types.StringListAttribute;
import jakarta.servlet.DispatcherType;

public class GzipHandlerFactory<R extends FactoryBase<?,R>> extends SimpleFactoryBase<Handler,R> {
    public final FactoryPolymorphicAttribute<Handler> handler = new FactoryPolymorphicAttribute<Handler>().labelText("Handler");
    public final IntegerAttribute minGzipSize = new IntegerAttribute().labelText("minGzipSize");
    public final EnumListAttribute<DispatcherType> dispatcherTypes = new EnumListAttribute<DispatcherType>().labelText("dispatcherTypes");
    public final StringListAttribute excludedMethods = new StringListAttribute().labelText("excludedMethods");
    public final StringListAttribute excludedMimeTypes = new StringListAttribute().labelText("excludedMimeTypes");
    public final StringListAttribute excludedPaths = new StringListAttribute().labelText("excludedPaths");
    public final StringListAttribute includedMethods = new StringListAttribute().labelText("includedMethods");
    public final StringListAttribute includedMimeTypes = new StringListAttribute().labelText("includedMimeTypes");
    public final StringListAttribute includedPaths = new StringListAttribute().labelText("includedPaths");
    public final IntegerAttribute inflateBufferSize = new IntegerAttribute().labelText("inflateBufferSize");
    public final BooleanAttribute syncFlush = new BooleanAttribute().labelText("syncFlush");


    @Override
    protected Handler createImpl() {
        GzipHandler gzipHandler = new GzipHandler();
        gzipHandler.setMinGzipSize(minGzipSize.get());
        gzipHandler.setDispatcherTypes(dispatcherTypes.get().toArray(new DispatcherType[0]));
        gzipHandler.setExcludedMethods(excludedMethods.get().toArray(new String[0]));
        gzipHandler.setExcludedMimeTypes(excludedMimeTypes.get().toArray(new String[0]));
        gzipHandler.setExcludedPaths(excludedPaths.get().toArray(new String[0]));
        gzipHandler.setIncludedMethods(includedMethods.get().toArray(new String[0]));
        gzipHandler.setIncludedMimeTypes(includedMimeTypes.get().toArray(new String[0]));
        gzipHandler.setIncludedPaths(includedPaths.get().toArray(new String[0]));
        gzipHandler.setInflateBufferSize(inflateBufferSize.get());
        gzipHandler.setSyncFlush(syncFlush.get());

        gzipHandler.setHandler(handler.instance());
        return gzipHandler;
    }
}

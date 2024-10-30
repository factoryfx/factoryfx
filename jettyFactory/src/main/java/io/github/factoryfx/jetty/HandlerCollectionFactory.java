package io.github.factoryfx.jetty;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryPolymorphicListAttribute;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;

public class HandlerCollectionFactory<R extends FactoryBase<?,R>> extends SimpleFactoryBase<ContextHandlerCollection,R> {
    public final FactoryPolymorphicListAttribute<Handler> handlers = new FactoryPolymorphicListAttribute<Handler>().labelText("Handlers");

    @Override
    protected ContextHandlerCollection createImpl() {
        return new ContextHandlerCollection(true,handlers.instances().stream().map(ContextHandler::new).toList().toArray(new ContextHandler[0]));
    }

    public HandlerCollectionFactory(){
        this.configLifeCycle().setUpdater(handlerCollection -> handlerCollection.setHandlers(handlers.instances().toArray(new Handler[0])));
    }
}

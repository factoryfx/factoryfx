package io.github.factoryfx.jetty;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryPolymorphicListAttribute;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.HandlerCollection;

public class HandlerCollectionFactory<R extends FactoryBase<?,R>> extends SimpleFactoryBase<HandlerCollection,R> {
    public final FactoryPolymorphicListAttribute<Handler> handlers = new FactoryPolymorphicListAttribute<Handler>().labelText("Handlers");

    @Override
    protected HandlerCollection createImpl() {
        return new HandlerCollection(true,handlers.instances().toArray(new Handler[0]));
    }

    public HandlerCollectionFactory(){
        this.configLifeCycle().setUpdater(handlerCollection -> handlerCollection.setHandlers(handlers.instances().toArray(new Handler[0])));
    }
}

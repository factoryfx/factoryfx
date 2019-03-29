package io.github.factoryfx.jetty;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.PolymorphicFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryPolymorphicReferenceListAttribute;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.HandlerCollection;

public class HandlerCollectionFactory<R extends FactoryBase<?,R>> extends PolymorphicFactoryBase<HandlerCollection,R> {
    public final FactoryPolymorphicReferenceListAttribute<R,Handler> handlers = new FactoryPolymorphicReferenceListAttribute<R,Handler>(Handler.class).labelText("Handlers");

    @Override
    public HandlerCollection createImpl() {
        return new HandlerCollection(true,handlers.instances().toArray(new Handler[0]));
    }

    public HandlerCollectionFactory(){
        this.configLifeCycle().setUpdater(handlerCollection -> handlerCollection.setHandlers(handlers.instances().toArray(new Handler[0])));
    }
}

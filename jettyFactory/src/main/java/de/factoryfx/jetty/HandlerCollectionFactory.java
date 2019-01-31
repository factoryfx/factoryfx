package de.factoryfx.jetty;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.PolymorphicFactoryBase;
import de.factoryfx.factory.atrribute.FactoryPolymorphicReferenceListAttribute;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.HandlerCollection;

public class HandlerCollectionFactory<V,R extends FactoryBase<?,V,R>> extends PolymorphicFactoryBase<HandlerCollection,V,R> {
    public final FactoryPolymorphicReferenceListAttribute<Handler> handlers = new FactoryPolymorphicReferenceListAttribute<>(Handler.class).labelText("Handlers");

    @Override
    public HandlerCollection createImpl() {
        return new HandlerCollection(true,handlers.instances().toArray(new Handler[0]));
    }

    public HandlerCollectionFactory(){
        this.configLifeCycle().setUpdater(handlerCollection -> handlerCollection.setHandlers(handlers.instances().toArray(new Handler[0])));
    }
}

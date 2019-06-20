package io.github.factoryfx.microservice.rest;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryPolymorphicAttribute;
import io.github.factoryfx.server.Microservice;
import io.github.factoryfx.server.user.nop.NoUserManagement;
import io.github.factoryfx.server.user.UserManagement;
import io.github.factoryfx.server.user.nop.NoUserManagementFactory;
import io.github.factoryfx.server.user.persistent.PersistentUserManagementFactory;

/**
 * usage example: (in a FactoryTreeBuilder)<br>
 * <pre>
 * {@code
    new JettyServerBuilder<>(new ShopJettyServerFactory())
        .withHost("localhost").withPort(8089)
        .withResource(context.getUnsafe(MicroserviceDomResourceFactory.class))
        ...

    factoryTreeBuilder.addFactory(MicroserviceDomResourceFactory.class, Scope.SINGLETON, context -> {
        return new MicroserviceDomResourceFactory();
    });
 * }
 * </pre>
 * (the messed up generics are caused by java limitations)
 *
 * @param <R> root
 * @param <S> Summary Data form storage history
 */
public class MicroserviceResourceFactory<R extends FactoryBase<?,R>,S> extends FactoryBase<MicroserviceResource<R,S>,R> {

    public final FactoryPolymorphicAttribute<R,UserManagement> userManagement = new FactoryPolymorphicAttribute<R,UserManagement>().setupUnsafe(UserManagement.class, NoUserManagementFactory.class, PersistentUserManagementFactory.class).labelText("resource").nullable();

    @SuppressWarnings("unchecked")
    public MicroserviceResourceFactory(){
        configLifeCycle().setCreator(() -> {
            UserManagement userManagementInstance = userManagement.instance();
            if (userManagementInstance==null) {
                userManagementInstance=new NoUserManagement();
            }
            Microservice<?,R,S> microservice = (Microservice<?,R,S>) utility().getMicroservice();
            return new MicroserviceResource<>(microservice, userManagementInstance);
        });

        config().setDisplayTextProvider(()->"Resource");
    }
}

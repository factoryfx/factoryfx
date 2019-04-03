package io.github.factoryfx.microservice.rest;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryPolymorphicReferenceAttribute;
import io.github.factoryfx.server.Microservice;
import io.github.factoryfx.server.user.nop.NoUserManagement;
import io.github.factoryfx.server.user.UserManagement;
import io.github.factoryfx.server.user.nop.NoUserManagementFactory;
import io.github.factoryfx.server.user.persistent.PersistentUserManagementFactory;

/**
 * usage example: (in a JettyserverFactory)<br>
 * <pre>
 * {@code
 *    public final FactoryReferenceAttribute<MicroserviceResource<Void, RootFactory,Void>, MicroserviceResourceFactory<Void,RootFactory,Void>> resource =
 *       new FactoryReferenceAttribute<>(MicroserviceResourceFactory.class);
 * }
 * </pre>
 * (the messed up generics are caused by java limitations)
 * <br>
 * Alternatively create a subclass<br>
 * <pre>
 * {@code
 *    public class ProjectNameMicroserviceResourceFactory extends MicroserviceResourceFactory<Void, RootFactory,Void> {
 *    }
 *    ...
 *    public final FactoryReferenceAttribute<MicroserviceResource<Void, RootFactory,Void>, ProjectNameMicroserviceResourceFactory> resource = new FactoryReferenceAttribute<>(ProjectNameMicroserviceResourceFactory.class));
 *
 * }
 * </pre>
 * @param <R> root
 * @param <S> Summary Data form storage history
 */
public class MicroserviceResourceFactory<R extends FactoryBase<?,R>,S> extends FactoryBase<MicroserviceResource<R,S>,R> {

    public final FactoryPolymorphicReferenceAttribute<R,UserManagement> userManagement = new FactoryPolymorphicReferenceAttribute<R,UserManagement>().setupUnsafe(UserManagement.class, NoUserManagementFactory.class, PersistentUserManagementFactory.class).labelText("resource").nullable();

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

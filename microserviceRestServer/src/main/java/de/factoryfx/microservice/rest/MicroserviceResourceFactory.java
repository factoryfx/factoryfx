package de.factoryfx.microservice.rest;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

import de.factoryfx.data.attribute.types.ObjectValueAttribute;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.atrribute.FactoryPolymorphicReferenceAttribute;
import de.factoryfx.server.Microservice;
import de.factoryfx.server.user.AuthorizedUser;
import de.factoryfx.server.user.nop.NoUserManagement;
import de.factoryfx.server.user.UserManagement;
import de.factoryfx.server.user.nop.NoUserManagementFactory;
import de.factoryfx.server.user.persistent.PersistentUserManagementFactory;

/**
 * usage example: (in a JettyserverFactory)<br>
 * {@code
 *         @SuppressWarnings("unchecked")
 *         public final FactoryReferenceAttribute<MicroserviceResource<Void, RootFactory,Void>, MicroserviceResourceFactory<Void,RootFactory,Void>> resource =
 *                 FactoryReferenceAttribute.create(new FactoryReferenceAttribute<>(MicroserviceResourceFactory.class));
 *}
 * <br><br>(the messed up generics are caused by java limitations)
 *
 * @param <V> visitor
 * @param <R> root
 * @param <S> Summary Data form storage history
 */
public class MicroserviceResourceFactory<V,R extends FactoryBase<?,V,R>,S> extends FactoryBase<MicroserviceResource<V,R,S>,V,R> {

    public final FactoryPolymorphicReferenceAttribute<UserManagement> userManagement = new FactoryPolymorphicReferenceAttribute<UserManagement>().setupUnsafe(UserManagement.class, NoUserManagementFactory.class, PersistentUserManagementFactory.class).labelText("resource").nullable();
    public final ObjectValueAttribute<Supplier<V>> emptyVisitorCreator= new ObjectValueAttribute<Supplier<V>>().labelText("emptyVisitorCreator").nullable();

    @SuppressWarnings("unchecked")
    public MicroserviceResourceFactory(){
        configLifeCycle().setCreator(() -> {
            UserManagement userManagementInstance = userManagement.instance();
            if (userManagementInstance==null) {
                userManagementInstance=new NoUserManagement();
            }
            Microservice<V, ?, R, S> microservice = (Microservice<V, ?, R, S>)utilityFactory().getMicroservice();
            return new MicroserviceResource<>(microservice, userManagementInstance,emptyVisitorCreator.get());
        });

        config().setDisplayTextProvider(()->"Resource");
    }
}

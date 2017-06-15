package de.factoryfx.server.rest;

import java.util.Optional;
import java.util.function.Predicate;

import de.factoryfx.data.attribute.types.ObjectValueAttribute;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.server.ApplicationServerAwareFactory;
import de.factoryfx.user.AuthorizedUser;
import de.factoryfx.user.NoUserManagement;
import de.factoryfx.user.UserManagement;
import de.factoryfx.user.persistent.PersistentUserManagementFactory;

public class ApplicationServerResourceFactory<V,L,T extends FactoryBase<L,V>> extends ApplicationServerAwareFactory<V,L,T,ApplicationServerResource> {

    ///TODO should be UserManagementFactory but doesn't work witch jackson
    public final FactoryReferenceAttribute<UserManagement,PersistentUserManagementFactory<V>> userManagement = new FactoryReferenceAttribute<UserManagement,PersistentUserManagementFactory<V>>().setupUnsafe(PersistentUserManagementFactory.class).labelText("resource");
    public final ObjectValueAttribute<Predicate<Optional<AuthorizedUser>>> authorizedKeyUserEvaluator= new ObjectValueAttribute<Predicate<Optional<AuthorizedUser>>>().labelText("authorizedKeyUserEvaluator");

    public ApplicationServerResourceFactory(){
        configLiveCycle().setCreator(() -> {
            Predicate<Optional<AuthorizedUser>> authorizedKeyUserEvaluator = this.authorizedKeyUserEvaluator.get();
            if (authorizedKeyUserEvaluator==null) {
                authorizedKeyUserEvaluator=(u)->true;
            }
            UserManagement userManagementInstance = userManagement.instance();
            if (userManagementInstance==null) {
                userManagementInstance=new NoUserManagement();
            }
            return new ApplicationServerResource<>(applicationServer.get(), userManagementInstance, authorizedKeyUserEvaluator);
        });

        config().setDisplayTextProvider(()->"Resource");
    }
}

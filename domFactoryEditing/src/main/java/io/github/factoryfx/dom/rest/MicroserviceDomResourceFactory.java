package io.github.factoryfx.dom.rest;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.FactoryTreeBuilderBasedAttributeSetup;
import io.github.factoryfx.factory.attribute.dependency.FactoryPolymorphicAttribute;
import io.github.factoryfx.factory.attribute.types.ObjectValueAttribute;
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
public class MicroserviceDomResourceFactory<R extends FactoryBase<?,R>,S> extends FactoryBase<MicroserviceDomResource<R,S>,R> {

    public final FactoryPolymorphicAttribute<R,UserManagement> userManagement = new FactoryPolymorphicAttribute<R,UserManagement>().setupUnsafe(UserManagement.class, NoUserManagementFactory.class, PersistentUserManagementFactory.class).labelText("resource").nullable();
    public final FactoryPolymorphicAttribute<R, StaticFileAccess> staticFileAccess = new FactoryPolymorphicAttribute<R, StaticFileAccess>().setupUnsafe(StaticFileAccess.class, ClasspathStaticFileAccessFactory.class).labelText("staticFileAccess").nullable();
    public final ObjectValueAttribute<FactoryTreeBuilderBasedAttributeSetup<R,S>> factoryTreeBuilderBasedAttributeSetup = new ObjectValueAttribute<>();



    @SuppressWarnings("unchecked")
    public MicroserviceDomResourceFactory(){
        configLifeCycle().setCreator(() -> {
            UserManagement userManagementInstance = userManagement.instance();
            if (userManagementInstance==null) {
                userManagementInstance=new NoUserManagement();
            }
            Microservice<?,R,S> microservice = (Microservice<?,R,S>) utility().getMicroservice();


            StaticFileAccess staticFileAccessInstance = staticFileAccess.instance();
            if (staticFileAccessInstance==null){
                staticFileAccessInstance=new ClasspathStaticFileAccess();
            }
            return new MicroserviceDomResource<>(microservice, userManagementInstance, staticFileAccessInstance, factoryTreeBuilderBasedAttributeSetup.get());
        });

        config().setDisplayTextProvider(()->"Microservice DOM Resource");
    }
}

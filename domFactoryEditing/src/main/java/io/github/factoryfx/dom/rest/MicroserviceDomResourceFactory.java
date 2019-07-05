package io.github.factoryfx.dom.rest;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.FactoryTreeBuilderBasedAttributeSetup;
import io.github.factoryfx.factory.attribute.dependency.FactoryPolymorphicAttribute;
import io.github.factoryfx.factory.attribute.types.ObjectValueAttribute;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.jetty.JerseyServletFactory;
import io.github.factoryfx.server.Microservice;
import io.github.factoryfx.server.user.nop.NoUserManagement;
import io.github.factoryfx.server.user.UserManagement;
import io.github.factoryfx.server.user.nop.NoUserManagementFactory;
import io.github.factoryfx.server.user.persistent.PersistentUserManagementFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

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
    public final ObjectValueAttribute<Function<R, List<GuiNavbarItem>>> guiNavbarItemCreator = new ObjectValueAttribute<>(ObjectValueAttribute::nullable);
    public final StringAttribute projectName = new StringAttribute().nullable();



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

            Function<R,List<GuiNavbarItem>> guiNavbarItemCreatorParam=guiNavbarItemCreator.get();
            if (guiNavbarItemCreatorParam==null){
                guiNavbarItemCreatorParam=(root)->{
                    ArrayList<GuiNavbarItem> result = new ArrayList<>();
                    result.add(new GuiNavbarItem("Root",root.getId().toString()));
                    for (FactoryBase<?, R> factory : root.internal().collectChildrenDeep()) {
                        if (factory instanceof JerseyServletFactory){
                            for (FactoryBase<?,R> resource : ((JerseyServletFactory<R>) factory).resources.get()) {
                                if (!(resource instanceof MicroserviceDomResourceFactory)){
                                    result.add(new GuiNavbarItem(resource.internal().getDisplayText(),resource.getId().toString()));
                                }
                            }
                        }
                    }

                    return result;
                };
            }
            return new MicroserviceDomResource<>(microservice, userManagementInstance, staticFileAccessInstance, factoryTreeBuilderBasedAttributeSetup.get(), guiNavbarItemCreatorParam,projectName.getNullable().orElse("Factoryfx"));
        });

        config().setDisplayTextProvider(()->"Microservice DOM Resource");
    }
}

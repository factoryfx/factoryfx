package io.github.factoryfx.factory.builder;

import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.storage.DataUpdate;
import io.github.factoryfx.factory.storage.migration.datamigration.PathBuilder;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.server.Microservice;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

public class RestoreMigrationTest {

    //----------------------------------old

    public static class ServerFactoryOld extends SimpleFactoryBase<Void, ServerFactoryOld> {

        public final FactoryAttribute<Void,PartnerFactoryOld> partnerFactory1 = new FactoryAttribute<>();
        public final FactoryAttribute<Void,PartnerFactoryOld> partnerFactory2 = new FactoryAttribute<>();

        public final FactoryAttribute<Void,ClientSystemFactoryOld> clientSystemFactory1 = new FactoryAttribute<>();
        public final FactoryAttribute<Void,ClientSystemFactoryOld> clientSystemFactory2 = new FactoryAttribute<>();

        @Override
        protected Void createImpl() {
            return null;
        }
    }

    public static class PartnerFactoryOld extends SimpleFactoryBase<Void, ServerFactoryOld> {
        public final StringAttribute url = new StringAttribute();


        @Override
        protected Void createImpl() {
            return null;
        }
    }

    public static class ClientSystemFactoryOld extends SimpleFactoryBase<Void, ServerFactoryOld> {
        public final StringAttribute url = new StringAttribute();

        @Override
        protected Void createImpl() {
            return null;
        }
    }

    //----------------------------------new


    public static class ServerFactory extends SimpleFactoryBase<Void, ServerFactory> {
        public final FactoryAttribute<Void, ClientSystemFactory> clientSystemFactory1 = new FactoryAttribute<>();
        public final FactoryAttribute<Void, ClientSystemFactory> clientSystemFactory2 = new FactoryAttribute<>();

        @Override
        protected Void createImpl() {
            return null;
        }
    }

    public static class ClientSystemFactory extends SimpleFactoryBase<Void, ServerFactory> {
        public final StringAttribute clientUrl = new StringAttribute().nullable();
        public final StringAttribute partnerUrl = new StringAttribute().nullable();

        @Override
        protected Void createImpl() {
            return null;
        }
    }


    @TempDir
    public Path folder;

    @Test
    public void test() {
        {
            FactoryTreeBuilder< Void, ServerFactoryOld> builderOld = new FactoryTreeBuilder<>(ServerFactoryOld.class, ctx -> {
                ServerFactoryOld serverFactoryOld = new ServerFactoryOld();
                serverFactoryOld.clientSystemFactory1.set(ctx.get(ClientSystemFactoryOld.class,"1"));
                serverFactoryOld.clientSystemFactory2.set(ctx.get(ClientSystemFactoryOld.class,"2"));
                serverFactoryOld.partnerFactory1.set(ctx.get(PartnerFactoryOld.class,"3"));
                serverFactoryOld.partnerFactory2.set(ctx.get(PartnerFactoryOld.class,"4"));
                return serverFactoryOld;
            });
            builderOld.addSingleton(ClientSystemFactoryOld.class,"1", ctx-> {
                ClientSystemFactoryOld clientSystemFactory=new ClientSystemFactoryOld();
                clientSystemFactory.url.set("1");
                return clientSystemFactory;
            });
            builderOld.addSingleton(ClientSystemFactoryOld.class,"2", ctx-> {
                ClientSystemFactoryOld clientSystemFactory=new ClientSystemFactoryOld();
                clientSystemFactory.url.set("2");
                return clientSystemFactory;
            });
            builderOld.addSingleton(PartnerFactoryOld.class, "3", ctx-> {
                PartnerFactoryOld serverFactoryNested = new PartnerFactoryOld();
                serverFactoryNested.url.set("X");
                return serverFactoryNested;
            });
            builderOld.addSingleton(PartnerFactoryOld.class, "4", ctx-> {
                PartnerFactoryOld serverFactoryNested = new PartnerFactoryOld();
                serverFactoryNested.url.set("Y");
                return serverFactoryNested;
            });


            Microservice<Void,ServerFactoryOld> msOld = builderOld.microservice().withFilesystemStorage(folder).build();
            msOld.start();

            DataUpdate<ServerFactoryOld> serverFactoryOldDataUpdate = msOld.prepareNewFactory();
            serverFactoryOldDataUpdate.root.partnerFactory1.get().url.set("3");
            serverFactoryOldDataUpdate.root.partnerFactory2.get().url.set("4");
            msOld.updateCurrentFactory(serverFactoryOldDataUpdate);

            msOld.stop();
        }



        FileSystemStorageTestUtil.patchClassName(folder);

        {
            FactoryTreeBuilder< Void, ServerFactory> builder = new FactoryTreeBuilder<>(ServerFactory.class,ctx -> {
                ServerFactory serverFactory = new ServerFactory();
                serverFactory.clientSystemFactory1.set(ctx.get(ClientSystemFactory.class,"1"));
                serverFactory.clientSystemFactory2.set(ctx.get(ClientSystemFactory.class,"2"));
                return serverFactory;
            });
            builder.addFactory(ClientSystemFactory.class, Scope.SINGLETON);
            builder.addSingleton(ClientSystemFactory.class,"1", ctx-> {
                ClientSystemFactory clientSystemFactory=new ClientSystemFactory();
                clientSystemFactory.clientUrl.set("1");
                return clientSystemFactory;
            });
            builder.addSingleton(ClientSystemFactory.class,"2", ctx-> {
                ClientSystemFactory clientSystemFactory=new ClientSystemFactory();
                clientSystemFactory.clientUrl.set("2");
                return clientSystemFactory;
            });

            Microservice<Void, ServerFactory> msNew = builder.microservice().withFilesystemStorage(folder).
                withRenameAttributeMigration(ClientSystemFactory.class, "url", (c) -> c.clientUrl).
                withRestoreAttributeMigration(String.class, PathBuilder.of("partnerFactory1.url"), (r, v) -> r.clientSystemFactory1.get().partnerUrl.set(v)).
                withRestoreAttributeMigration(String.class, (path)->path.pathElement("partnerFactory2").attribute("url"), (r, v) -> r.clientSystemFactory2.get().partnerUrl.set(v)).
            build();
            msNew.start();

            ServerFactory serverFactory = msNew.prepareNewFactory().root;
            Assertions.assertEquals("3",serverFactory.clientSystemFactory1.get().partnerUrl.get());
            Assertions.assertEquals("1",serverFactory.clientSystemFactory1.get().clientUrl.get());
            Assertions.assertEquals("4",serverFactory.clientSystemFactory2.get().partnerUrl.get());
            Assertions.assertEquals("2",serverFactory.clientSystemFactory2.get().clientUrl.get());
            msNew.stop();
        }
    }



    @Test
    public void test_treeBuilder_data_migration() {
        {
            FactoryTreeBuilder< Void, ServerFactoryOld> builderOld = new FactoryTreeBuilder<>(ServerFactoryOld.class, ctx -> {
                ServerFactoryOld serverFactoryOld = new ServerFactoryOld();
                serverFactoryOld.clientSystemFactory1.set(ctx.get(ClientSystemFactoryOld.class,"1"));
                serverFactoryOld.clientSystemFactory2.set(ctx.get(ClientSystemFactoryOld.class,"2"));
                serverFactoryOld.partnerFactory1.set(ctx.get(PartnerFactoryOld.class,"3"));
                serverFactoryOld.partnerFactory2.set(ctx.get(PartnerFactoryOld.class,"4"));
                return serverFactoryOld;
            });
            builderOld.addSingleton(ClientSystemFactoryOld.class,"1", ctx-> {
                ClientSystemFactoryOld clientSystemFactory=new ClientSystemFactoryOld();
                clientSystemFactory.url.set("1");
                return clientSystemFactory;
            });
            builderOld.addSingleton(ClientSystemFactoryOld.class,"2", ctx-> {
                ClientSystemFactoryOld clientSystemFactory=new ClientSystemFactoryOld();
                clientSystemFactory.url.set("2");
                return clientSystemFactory;
            });
            builderOld.addSingleton(PartnerFactoryOld.class, "3", ctx-> {
                PartnerFactoryOld serverFactoryNested = new PartnerFactoryOld();
                serverFactoryNested.url.set("X");
                return serverFactoryNested;
            });
            builderOld.addSingleton(PartnerFactoryOld.class, "4", ctx-> {
                PartnerFactoryOld serverFactoryNested = new PartnerFactoryOld();
                serverFactoryNested.url.set("Y");
                return serverFactoryNested;
            });


            Microservice<Void,ServerFactoryOld> msOld = builderOld.microservice().withFilesystemStorage(folder).build();
            msOld.start();

            DataUpdate<ServerFactoryOld> serverFactoryOldDataUpdate = msOld.prepareNewFactory();
            serverFactoryOldDataUpdate.root.partnerFactory1.get().url.set("3");
            serverFactoryOldDataUpdate.root.partnerFactory2.get().url.set("4");
            msOld.updateCurrentFactory(serverFactoryOldDataUpdate);

            msOld.stop();
        }



        FileSystemStorageTestUtil.patchClassName(folder);

        {
            FactoryTreeBuilder< Void, ServerFactory> builder = new FactoryTreeBuilder<>(ServerFactory.class,ctx -> {
                ServerFactory serverFactory = new ServerFactory();
                serverFactory.clientSystemFactory1.set(ctx.get(ClientSystemFactory.class,"1"));
                serverFactory.clientSystemFactory2.set(ctx.get(ClientSystemFactory.class,"2"));
                return serverFactory;
            });
            builder.addFactory(ClientSystemFactory.class, Scope.SINGLETON);
            builder.addSingleton(ClientSystemFactory.class,"1", ctx-> {
                ClientSystemFactory clientSystemFactory=new ClientSystemFactory();
                clientSystemFactory.clientUrl.set("1");
                clientSystemFactory.partnerUrl.set("override3");
                return clientSystemFactory;
            });
            builder.addSingleton(ClientSystemFactory.class,"2", ctx-> {
                ClientSystemFactory clientSystemFactory=new ClientSystemFactory();
                clientSystemFactory.clientUrl.set("2");
                clientSystemFactory.partnerUrl.set("override4");
                return clientSystemFactory;
            });

            Microservice<Void, ServerFactory> msNew = builder.microservice().withFilesystemStorage(folder).
                    withRenameAttributeMigration(ClientSystemFactory.class, "url", (c) -> c.clientUrl).
                    withRestoreAttributeMigration(String.class, PathBuilder.of("partnerFactory1.url"), (r, v) -> r.clientSystemFactory1.get().partnerUrl.set(v)).
                    withRestoreAttributeMigration(String.class, (path)->path.pathElement("partnerFactory2").attribute("url"), (r, v) -> r.clientSystemFactory2.get().partnerUrl.set(v)).
                    build();
            msNew.start();

            ServerFactory serverFactory = msNew.prepareNewFactory().root;
            Assertions.assertEquals("3",serverFactory.clientSystemFactory1.get().partnerUrl.get());
            Assertions.assertEquals("1",serverFactory.clientSystemFactory1.get().clientUrl.get());
            Assertions.assertEquals("4",serverFactory.clientSystemFactory2.get().partnerUrl.get());
            Assertions.assertEquals("2",serverFactory.clientSystemFactory2.get().clientUrl.get());
            msNew.stop();
        }
    }
}

package io.github.factoryfx.factory.builder;

import io.github.factoryfx.data.attribute.types.StringAttribute;
import io.github.factoryfx.data.storage.migration.datamigration.PathBuilder;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import io.github.factoryfx.server.Microservice;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class RestoreMigrationTest {

    //----------------------------------old

    public static class ServerFactoryOld extends SimpleFactoryBase<Void, ServerFactoryOld> {

        public final FactoryReferenceAttribute<Void,PartnerFactoryOld>  partnerFactory1 = new FactoryReferenceAttribute<>(PartnerFactoryOld.class);
        public final FactoryReferenceAttribute<Void,PartnerFactoryOld>  partnerFactory2 = new FactoryReferenceAttribute<>(PartnerFactoryOld.class);

        public final FactoryReferenceAttribute<Void,ClientSystemFactoryOld>  clientSystemFactory1 = new FactoryReferenceAttribute<>(ClientSystemFactoryOld.class);
        public final FactoryReferenceAttribute<Void,ClientSystemFactoryOld>  clientSystemFactory2 = new FactoryReferenceAttribute<>(ClientSystemFactoryOld.class);

        @Override
        public Void createImpl() {
            return null;
        }
    }


    public static class ServerFactoryNestedOld extends SimpleFactoryBase<Void, ServerFactoryOld> {

        public final FactoryReferenceAttribute<Void,PartnerFactoryOld>  partnerFactory1 = new FactoryReferenceAttribute<>(PartnerFactoryOld.class);
        public final FactoryReferenceAttribute<Void,PartnerFactoryOld>  partnerFactory2 = new FactoryReferenceAttribute<>(PartnerFactoryOld.class);

        public final FactoryReferenceAttribute<Void,ClientSystemFactoryOld>  clientSystemFactory1 = new FactoryReferenceAttribute<>(ClientSystemFactoryOld.class);
        public final FactoryReferenceAttribute<Void,ClientSystemFactoryOld>  clientSystemFactory2 = new FactoryReferenceAttribute<>(ClientSystemFactoryOld.class);

        @Override
        public Void createImpl() {
            return null;
        }
    }


    public static class PartnerFactoryOld extends SimpleFactoryBase<Void, ServerFactoryOld> {
        public final StringAttribute url = new StringAttribute();


        @Override
        public Void createImpl() {
            return null;
        }
    }

    public static class ClientSystemFactoryOld extends SimpleFactoryBase<Void, ServerFactoryOld> {
        public final StringAttribute url = new StringAttribute();

        @Override
        public Void createImpl() {
            return null;
        }
    }

    //----------------------------------new


    public static class ServerFactory extends SimpleFactoryBase<Void, ServerFactory> {
        public final FactoryReferenceAttribute<Void, ClientSystemFactory>  clientSystemFactory1 = new FactoryReferenceAttribute<>(ClientSystemFactory.class);
        public final FactoryReferenceAttribute<Void, ClientSystemFactory>  clientSystemFactory2 = new FactoryReferenceAttribute<>(ClientSystemFactory.class);

        @Override
        public Void createImpl() {
            return null;
        }
    }

    public static class ClientSystemFactory extends SimpleFactoryBase<Void, ServerFactory> {
        public final StringAttribute clientUrl = new StringAttribute().nullable();
        public final StringAttribute partnerUrl = new StringAttribute().nullable();

        @Override
        public Void createImpl() {
            return null;
        }
    }


    @TempDir
    public Path folder;

    @Test
    public void test() throws IOException {
        {
            FactoryTreeBuilder< Void, ServerFactoryOld, Void> builderOld = new FactoryTreeBuilder<>(ServerFactoryOld.class);
            builderOld.addFactory(ServerFactoryOld.class, Scope.SINGLETON, ctx -> {
                ServerFactoryOld serverFactoryOld = new ServerFactoryOld();
                serverFactoryOld.clientSystemFactory1.set(new ClientSystemFactoryOld());
                serverFactoryOld.clientSystemFactory2.set(new ClientSystemFactoryOld());
                serverFactoryOld.partnerFactory1.set(new PartnerFactoryOld());
                serverFactoryOld.partnerFactory2.set(new PartnerFactoryOld());
                serverFactoryOld.clientSystemFactory1.get().url.set("1");
                serverFactoryOld.clientSystemFactory2.get().url.set("2");
                serverFactoryOld.partnerFactory1.get().url.set("3");
                serverFactoryOld.partnerFactory2.get().url.set("4");
                return serverFactoryOld;
            });
            Microservice<Void,ServerFactoryOld,Void> msOld = builderOld.microservice().withFilesystemStorage(folder).build();
            msOld.start();
            msOld.stop();
        }



        String currentFactory=Files.readString(folder.resolve("currentFactory.json"));
        currentFactory=currentFactory.replace("Old","");
        System.out.println(currentFactory);
        Files.writeString(folder.resolve("currentFactory.json"),currentFactory);

        String currentFactorymetadata=Files.readString(folder.resolve("currentFactory_metadata.json"));
        currentFactorymetadata=currentFactorymetadata.replace("Old","");
        System.out.println(currentFactorymetadata);
        Files.writeString(folder.resolve("currentFactory_metadata.json"),currentFactorymetadata);

        {
            FactoryTreeBuilder< Void, ServerFactory, Void> builder = new FactoryTreeBuilder<>(ServerFactory.class);
            builder.addFactory(ServerFactory.class, Scope.SINGLETON);
            builder.addFactory(ClientSystemFactory.class, Scope.SINGLETON);
            Microservice<Void, ServerFactory, Void> msNew = builder.microservice().withFilesystemStorage(folder).
                withRenameAttributeMigration(ClientSystemFactory.class, "url", (c) -> c.clientUrl).
                withRestoreAttributeMigration(PathBuilder.value(String.class).pathElement("partnerFactory1").attribute("url"), (r, v) -> r.clientSystemFactory1.get().partnerUrl.set(v)).
                withRestoreAttributeMigration(PathBuilder.value(String.class).pathElement("partnerFactory2").attribute("url"), (r, v) -> r.clientSystemFactory2.get().partnerUrl.set(v)).
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

package io.github.factoryfx.factory.builder;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryReferenceAttribute;
import io.github.factoryfx.server.Microservice;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ReferenceTypeChangeMigrationTest {

    //----------------------------------old

    public static class ServerFactoryOld extends SimpleFactoryBase<Void, ServerFactoryOld> {
        public final FactoryReferenceAttribute<ServerFactoryOld,Void,PartnerFactory>  partnerFactory = new FactoryReferenceAttribute<>();

        @Override
        public Void createImpl() {
            return null;
        }
    }

    public static class PartnerFactory extends SimpleFactoryBase<Void, ServerFactoryOld> {
        @Override
        public Void createImpl() {
            return null;
        }
    }

    //----------------------------------new


    public static class ServerFactory extends SimpleFactoryBase<Void, ServerFactory> {
        public final FactoryReferenceAttribute<ServerFactory,Void,ClientSystemFactory>  partnerFactory = new FactoryReferenceAttribute<>();

        @Override
        public Void createImpl() {
            return null;
        }
    }

    public static class ClientSystemFactory extends SimpleFactoryBase<Void, ServerFactory> {


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
            FactoryTreeBuilder<Void, ServerFactoryOld, Void> builderOld = new FactoryTreeBuilder<>(ServerFactoryOld.class);
            builderOld.addFactory(ServerFactoryOld.class, Scope.SINGLETON, ctx -> {
                ServerFactoryOld serverFactoryOld = new ServerFactoryOld();
                serverFactoryOld.partnerFactory.set(new PartnerFactory());
                return serverFactoryOld;
            });
            Microservice<Void, ServerFactoryOld, Void> msOld = builderOld.microservice().withFilesystemStorage(folder).build();
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
                    withRenameClassMigration(PartnerFactory.class.getName(),ClientSystemFactory.class).
                    build();
            msNew.start();

            ServerFactory serverFactory = msNew.prepareNewFactory().root;
            Assertions.assertNotNull(serverFactory.partnerFactory.get());
            msNew.stop();
        }
    }
}

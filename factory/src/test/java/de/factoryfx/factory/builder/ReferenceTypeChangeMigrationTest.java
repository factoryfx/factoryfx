package de.factoryfx.factory.builder;

import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.server.Microservice;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ReferenceTypeChangeMigrationTest {

    //----------------------------------old

    public static class ServerFactoryOld extends SimpleFactoryBase<Void,Void, ServerFactoryOld> {
        public final FactoryReferenceAttribute<Void,PartnerFactory>  partnerFactory = new FactoryReferenceAttribute<>(PartnerFactory.class);

        @Override
        public Void createImpl() {
            return null;
        }
    }

    public static class PartnerFactory extends SimpleFactoryBase<Void,Void, ServerFactoryOld> {
        @Override
        public Void createImpl() {
            return null;
        }
    }

    //----------------------------------new


    public static class ServerFactory extends SimpleFactoryBase<Void,Void, ServerFactory> {
        public final FactoryReferenceAttribute<Void,ClientSystemFactory>  partnerFactory = new FactoryReferenceAttribute<>(ClientSystemFactory.class);

        @Override
        public Void createImpl() {
            return null;
        }
    }

    public static class ClientSystemFactory extends SimpleFactoryBase<Void,Void, ServerFactory> {


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
            FactoryTreeBuilder<Void, Void, ServerFactoryOld, Void> builderOld = new FactoryTreeBuilder<>(ServerFactoryOld.class);
            builderOld.addFactory(ServerFactoryOld.class, Scope.SINGLETON, ctx -> {
                ServerFactoryOld serverFactoryOld = new ServerFactoryOld();
                serverFactoryOld.partnerFactory.set(new PartnerFactory());
                return serverFactoryOld;
            });
            Microservice<Void, Void, ServerFactoryOld, Void> msOld = builderOld.microservice().withFilesystemStorage(folder).build();
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
            FactoryTreeBuilder<Void, Void, ServerFactory, Void> builder = new FactoryTreeBuilder<>(ServerFactory.class);
            builder.addFactory(ServerFactory.class, Scope.SINGLETON);
            builder.addFactory(ClientSystemFactory.class, Scope.SINGLETON);
            Microservice<Void, Void, ServerFactory, Void> msNew = builder.microservice().withFilesystemStorage(folder).
                    withRenameClassMigration(PartnerFactory.class.getName(),ClientSystemFactory.class).
                    build();
            msNew.start();

            ServerFactory serverFactory = msNew.prepareNewFactory().root;
            Assertions.assertNotNull(serverFactory.partnerFactory.get());
            msNew.stop();
        }
    }
}

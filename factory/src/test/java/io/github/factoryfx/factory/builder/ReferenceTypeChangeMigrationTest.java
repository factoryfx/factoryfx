package io.github.factoryfx.factory.builder;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.server.Microservice;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

public class ReferenceTypeChangeMigrationTest {

    //----------------------------------old

    public static class ServerFactoryOld extends SimpleFactoryBase<Void, ServerFactoryOld> {
        public final FactoryAttribute<Void,PartnerFactory> partnerFactory = new FactoryAttribute<>();

        @Override
        protected Void createImpl() {
            return null;
        }
    }

    public static class PartnerFactory extends SimpleFactoryBase<Void, ServerFactoryOld> {
        @Override
        protected Void createImpl() {
            return null;
        }
    }

    //----------------------------------new


    public static class ServerFactory extends SimpleFactoryBase<Void, ServerFactory> {
        public final FactoryAttribute<Void,ClientSystemFactory> partnerFactory = new FactoryAttribute<>();

        @Override
        protected Void createImpl() {
            return null;
        }
    }

    public static class ClientSystemFactory extends SimpleFactoryBase<Void, ServerFactory> {


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
            FactoryTreeBuilder<Void, ServerFactoryOld> builderOld = new FactoryTreeBuilder<>(ServerFactoryOld.class, ctx -> {
                ServerFactoryOld serverFactoryOld = new ServerFactoryOld();
                serverFactoryOld.partnerFactory.set(new PartnerFactory());
                return serverFactoryOld;
            });
            Microservice<Void, ServerFactoryOld> msOld = builderOld.microservice().withFilesystemStorage(folder).build();
            msOld.start();
            msOld.stop();
        }

        FileSystemStorageTestUtil.patchClassName(folder);

        {
            FactoryTreeBuilder< Void, ServerFactory> builder = new FactoryTreeBuilder<>(ServerFactory.class);
            builder.addFactory(ClientSystemFactory.class, Scope.SINGLETON);
            Microservice<Void, ServerFactory> msNew = builder.microservice().withFilesystemStorage(folder).
                    withRenameClassMigration(PartnerFactory.class.getName(),ClientSystemFactory.class).
                    build();
            msNew.start();

            ServerFactory serverFactory = msNew.prepareNewFactory().root;
            Assertions.assertNotNull(serverFactory.partnerFactory.get());
            msNew.stop();
        }
    }
}

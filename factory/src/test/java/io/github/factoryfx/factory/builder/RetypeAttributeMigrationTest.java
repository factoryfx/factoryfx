package io.github.factoryfx.factory.builder;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.primitive.IntegerAttribute;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.attribute.types.StringListAttribute;
import io.github.factoryfx.factory.storage.DataUpdate;
import io.github.factoryfx.server.Microservice;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class RetypeAttributeMigrationTest {

    //----------------------------------old

    public static class ServerFactoryOld extends SimpleFactoryBase<Void, ServerFactoryOld> {
        public final StringAttribute csv = new StringAttribute().nullable();
        public final IntegerAttribute number = new IntegerAttribute().nullable();
        public final StringListAttribute tags = new StringListAttribute();

        @Override
        protected Void createImpl() {
            return null;
        }
    }

    //----------------------------------new: csv String->StringList, number Integer->String, tags StringList->String

    public static class ServerFactory extends SimpleFactoryBase<Void, ServerFactory> {
        public final StringListAttribute csv = new StringListAttribute();
        public final StringAttribute number = new StringAttribute().nullable();
        public final StringAttribute tags = new StringAttribute().nullable();

        @Override
        protected Void createImpl() {
            return null;
        }
    }

    @TempDir
    public Path folder;

    private void createOldConfiguration(String csv, Integer number, List<String> tags) {
        FactoryTreeBuilder<Void, ServerFactoryOld> builderOld = new FactoryTreeBuilder<>(ServerFactoryOld.class, ctx -> {
            ServerFactoryOld factory = new ServerFactoryOld();
            factory.csv.set(csv);
            factory.number.set(number);
            factory.tags.set(tags);
            return factory;
        });
        Microservice<Void, ServerFactoryOld> microserviceOld = builderOld.microservice().withFilesystemStorage(folder).build();
        microserviceOld.start();
        microserviceOld.stop();

        FileSystemStorageTestUtil.patchClassName(folder);
    }

    private FactoryTreeBuilder<Void, ServerFactory> createNewBuilder() {
        return new FactoryTreeBuilder<>(ServerFactory.class, ctx -> new ServerFactory());
    }

    @Test
    public void test_automaticConversions() {
        createOldConfiguration("a,b", 42, List.of("x"));

        Microservice<Void, ServerFactory> microservice = createNewBuilder().microservice().withFilesystemStorage(folder).build();
        microservice.start();
        ServerFactory root = microservice.prepareNewFactory().root;
        //single->list: value wrapped in a list
        Assertions.assertEquals(List.of("a,b"), root.csv.get());
        //number->string
        Assertions.assertEquals("42", root.number.get());
        //list with one element->single value
        Assertions.assertEquals("x", root.tags.get());
        microservice.stop();
    }

    @Test
    public void test_automaticConversion_lossyListToSingle_cleared() {
        createOldConfiguration(null, null, List.of("x", "y"));

        Microservice<Void, ServerFactory> microservice = createNewBuilder().microservice().withFilesystemStorage(folder).build();
        microservice.start();
        ServerFactory root = microservice.prepareNewFactory().root;
        //list with more than one element can't be converted automatically, value is cleared like before
        Assertions.assertNull(root.tags.get());
        microservice.stop();
    }

    @Test
    public void test_explicitRetypeMigration_withConverter() {
        createOldConfiguration("a,b", null, List.of("x", "y"));

        Microservice<Void, ServerFactory> microservice = createNewBuilder().microservice().withFilesystemStorage(folder)
                .withRetypeAttributeMigration(ServerFactory.class, String.class, (f) -> f.csv, (String old) -> old == null ? null : Arrays.asList(old.split(",")))
                .withRetypeListAttributeMigration(ServerFactory.class, String.class, (f) -> f.tags, (List<String> old) -> old == null ? null : String.join(",", old))
                .build();
        microservice.start();
        ServerFactory root = microservice.prepareNewFactory().root;
        Assertions.assertEquals(List.of("a", "b"), root.csv.get());
        Assertions.assertEquals("x,y", root.tags.get());
        microservice.stop();
    }

    @Test
    public void test_explicitRetypeMigration_selfSkipsAfterResave() {
        createOldConfiguration("a,b", null, List.of());

        {
            Microservice<Void, ServerFactory> microservice = createNewBuilder().microservice().withFilesystemStorage(folder)
                    .withRetypeAttributeMigration(ServerFactory.class, String.class, (f) -> f.csv, (String old) -> old == null ? null : Arrays.asList(old.split(",")))
                    .build();
            microservice.start();
            DataUpdate<ServerFactory> update = microservice.prepareNewFactory();
            update.root.number.set("saved");
            microservice.updateCurrentFactory(update);
            microservice.stop();
        }

        //after re-save the stored schema matches, the migration self-skips and the converted value is preserved
        Microservice<Void, ServerFactory> microservice = createNewBuilder().microservice().withFilesystemStorage(folder)
                .withRetypeAttributeMigration(ServerFactory.class, String.class, (f) -> f.csv, (String old) -> {
                    throw new IllegalStateException("migration must not run for the already converted configuration");
                })
                .build();
        microservice.start();
        ServerFactory root = microservice.prepareNewFactory().root;
        Assertions.assertEquals(List.of("a", "b"), root.csv.get());
        Assertions.assertEquals("saved", root.number.get());
        microservice.stop();
    }
}

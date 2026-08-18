package io.github.factoryfx.factory.builder;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryListAttribute;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.jackson.SimpleObjectMapper;
import io.github.factoryfx.server.Microservice;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

/**
 * a deprecated reference list attribute is replaced by a single reference attribute with a new name
 * (old and new attribute coexisted during the transition, then the old one is removed):
 * withRenameAttributeMigration + the automatic reference retype conversion (list to single with unchanged
 * reference class) migrate the stored data without a hand-written patch
 */
public class ReferenceRenameRetypeMigrationTest {

    //----------------------------------old model: deprecated list attribute 'items' plus its replacement 'item'

    public static class SharedFactoryOld extends SimpleFactoryBase<Void, ServerFactoryOld> {
        public final StringAttribute name = new StringAttribute().nullable();

        @Override
        protected Void createImpl() {
            return null;
        }
    }

    public static class ServerFactoryOld extends SimpleFactoryBase<Void, ServerFactoryOld> {
        public final FactoryListAttribute<Void, SharedFactoryOld> items = new FactoryListAttribute<>();
        public final FactoryAttribute<Void, SharedFactoryOld> item = new FactoryAttribute<Void, SharedFactoryOld>().nullable();

        @Override
        protected Void createImpl() {
            return null;
        }
    }

    //----------------------------------new model: only the single reference remains

    public static class SharedFactory extends SimpleFactoryBase<Void, ServerFactory> {
        public final StringAttribute name = new StringAttribute().nullable();

        @Override
        protected Void createImpl() {
            return null;
        }
    }

    public static class ServerFactory extends SimpleFactoryBase<Void, ServerFactory> {
        public final FactoryAttribute<Void, SharedFactory> item = new FactoryAttribute<Void, SharedFactory>().nullable();

        @Override
        protected Void createImpl() {
            return null;
        }
    }

    @TempDir
    public Path folder;

    private void createOldConfiguration(java.util.function.Consumer<ServerFactoryOld> setup) {
        FactoryTreeBuilder<Void, ServerFactoryOld> builderOld = new FactoryTreeBuilder<>(ServerFactoryOld.class, ctx -> {
            ServerFactoryOld factory = new ServerFactoryOld();
            setup.accept(factory);
            return factory;
        });
        builderOld.markAsNonPersistentFactoryBuilder();
        Microservice<Void, ServerFactoryOld> microserviceOld = builderOld.microservice().withFilesystemStorage(folder).build();
        microserviceOld.start();
        microserviceOld.stop();

        FileSystemStorageTestUtil.patchClassName(folder);
    }

    private static SharedFactoryOld shared(String name) {
        SharedFactoryOld shared = new SharedFactoryOld();
        shared.name.set(name);
        return shared;
    }

    private MicroserviceBuilder<Void, ServerFactory> createNewMicroserviceBuilder() {
        return new FactoryTreeBuilder<Void, ServerFactory>(ServerFactory.class, ctx -> new ServerFactory())
                .microservice().withFilesystemStorage(folder)
                .withRenameAttributeMigration(ServerFactory.class, "items", f -> f.item);
    }

    @Test
    public void test_listMovedToRenamedSingleReference() {
        createOldConfiguration(factory -> factory.items.add(shared("shared-value")));

        Microservice<Void, ServerFactory> microservice = createNewMicroserviceBuilder().build();
        microservice.start();
        ServerFactory root = microservice.prepareNewFactory().root;
        Assertions.assertNotNull(root.item.get());
        Assertions.assertEquals("shared-value", root.item.get().name.get());
        microservice.stop();
    }

    @Test
    public void test_renameDoesNotClobberExistingTargetValue() {
        createOldConfiguration(factory -> factory.item.set(shared("already-migrated")));

        Microservice<Void, ServerFactory> microservice = createNewMicroserviceBuilder().build();
        microservice.start();
        ServerFactory root = microservice.prepareNewFactory().root;
        Assertions.assertNotNull(root.item.get());
        Assertions.assertEquals("already-migrated", root.item.get().name.get());
        microservice.stop();
    }

    @Test
    public void test_bothSet_targetWins_oldValueDropped() {
        createOldConfiguration(factory -> {
            factory.items.add(shared("stale"));
            factory.item.set(shared("current"));
        });

        Microservice<Void, ServerFactory> microservice = createNewMicroserviceBuilder().build();
        microservice.start();
        ServerFactory root = microservice.prepareNewFactory().root;
        Assertions.assertNotNull(root.item.get());
        Assertions.assertEquals("current", root.item.get().name.get());
        microservice.stop();
    }

    @Test
    public void test_listWithMultipleElements_firstElementUsed() {
        createOldConfiguration(factory -> {
            factory.items.add(shared("one"));
            factory.items.add(shared("two"));
        });

        Microservice<Void, ServerFactory> microservice = createNewMicroserviceBuilder().build();
        microservice.start();
        ServerFactory root = microservice.prepareNewFactory().root;
        //a list with more than one element is converted to its first element, the rest is dropped (with a warning)
        Assertions.assertNotNull(root.item.get());
        Assertions.assertEquals("one", root.item.get().name.get());
        microservice.stop();
    }

    @Test
    public void test_migrationSurvivesRoundtrippingPatch() {
        createOldConfiguration(factory -> factory.items.add(shared("shared-value")));

        //the declarative migrations run before the patches: the roundtrip through the current classes
        //(which would silently drop the removed/renamed list attribute) sees the already migrated json
        Microservice<Void, ServerFactory> microservice = createNewMicroserviceBuilder()
                .withPatch(0, 1, (root, metadata, objectMapper) -> {
                    ObjectMapper mapper = ObjectMapperBuilder.buildNewObjectMapper();
                    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    SimpleObjectMapper simpleObjectMapper = new SimpleObjectMapper(mapper);
                    ServerFactory serverFactory = simpleObjectMapper.treeToValue(root.getJsonNode(), ServerFactory.class);
                    serverFactory.internal().finalise();
                    JsonNode jsonNode = simpleObjectMapper.valueToTree(serverFactory);
                    root.getJsonNode().properties().clear();
                    jsonNode.properties().forEach(entry -> root.getJsonNode().set(entry.getKey(), entry.getValue()));
                })
                .build();
        microservice.start();
        ServerFactory root = microservice.prepareNewFactory().root;
        Assertions.assertNotNull(root.item.get());
        Assertions.assertEquals("shared-value", root.item.get().name.get());
        microservice.stop();
    }
}

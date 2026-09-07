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
 * removing an attribute (or a whole factory class) whose stored json holds the first occurrence of a factory that is
 * still referenced elsewhere must not lose that factory: the definition is relocated to its first remaining reference
 * on load, before the registered configuration patches run
 */
public class RemovedAttributeRelocationTest {

    //----------------------------------old model

    public static class SharedFactoryOld extends SimpleFactoryBase<Void, ServerFactoryOld> {
        public final StringAttribute name = new StringAttribute().nullable();

        @Override
        protected Void createImpl() {
            return null;
        }
    }

    public static class ContainerFactoryOld extends SimpleFactoryBase<Void, ServerFactoryOld> {
        public final FactoryAttribute<Void, SharedFactoryOld> inner = new FactoryAttribute<Void, SharedFactoryOld>().nullable();

        @Override
        protected Void createImpl() {
            return null;
        }
    }

    public static class ServerFactoryOld extends SimpleFactoryBase<Void, ServerFactoryOld> {
        //declared first: hold the first occurrence (the full definition) when set
        public final FactoryAttribute<Void, ContainerFactoryOld> removedContainer = new FactoryAttribute<Void, ContainerFactoryOld>().nullable();
        public final FactoryAttribute<Void, SharedFactoryOld> removedRef = new FactoryAttribute<Void, SharedFactoryOld>().nullable();
        public final FactoryAttribute<Void, SharedFactoryOld> keptRef = new FactoryAttribute<Void, SharedFactoryOld>().nullable();
        public final FactoryListAttribute<Void, SharedFactoryOld> keptList = new FactoryListAttribute<>();

        @Override
        protected Void createImpl() {
            return null;
        }
    }

    //----------------------------------new model: removedContainer/removedRef attributes removed, ContainerFactory class removed

    public static class SharedFactory extends SimpleFactoryBase<Void, ServerFactory> {
        public final StringAttribute name = new StringAttribute().nullable();

        @Override
        protected Void createImpl() {
            return null;
        }
    }

    public static class ServerFactory extends SimpleFactoryBase<Void, ServerFactory> {
        public final FactoryAttribute<Void, SharedFactory> keptRef = new FactoryAttribute<Void, SharedFactory>().nullable();
        public final FactoryListAttribute<Void, SharedFactory> keptList = new FactoryListAttribute<>();

        @Override
        protected Void createImpl() {
            return null;
        }
    }

    @TempDir
    public Path folder;

    private void createOldConfiguration(boolean setContainer, boolean setRemovedRef, boolean setKeptRef, boolean setKeptList) {
        FactoryTreeBuilder<Void, ServerFactoryOld> builderOld = new FactoryTreeBuilder<>(ServerFactoryOld.class, ctx -> {
            ServerFactoryOld factory = new ServerFactoryOld();
            SharedFactoryOld shared = new SharedFactoryOld();
            shared.name.set("shared-value");
            if (setContainer) {
                ContainerFactoryOld container = new ContainerFactoryOld();
                container.inner.set(shared);
                factory.removedContainer.set(container);
            }
            if (setRemovedRef) {
                factory.removedRef.set(shared);
            }
            if (setKeptRef) {
                factory.keptRef.set(shared);
            }
            if (setKeptList) {
                factory.keptList.add(shared);
            }
            return factory;
        });
        builderOld.markAsNonPersistentFactoryBuilder();
        Microservice<Void, ServerFactoryOld> microserviceOld = builderOld.microservice().withFilesystemStorage(folder).build();
        microserviceOld.start();
        microserviceOld.stop();

        FileSystemStorageTestUtil.patchClassName(folder);
    }

    private FactoryTreeBuilder<Void, ServerFactory> createNewBuilder() {
        return new FactoryTreeBuilder<>(ServerFactory.class, ctx -> new ServerFactory());
    }

    @Test
    public void test_definitionRelocatedToRemainingReference() {
        createOldConfiguration(false, true, true, false);

        Microservice<Void, ServerFactory> microservice = createNewBuilder().microservice().withFilesystemStorage(folder).build();
        microservice.start();
        ServerFactory root = microservice.prepareNewFactory().root;
        Assertions.assertNotNull(root.keptRef.get());
        Assertions.assertEquals("shared-value", root.keptRef.get().name.get());
        microservice.stop();
    }

    @Test
    public void test_definitionSurvivesRoundtrippingPatch() {
        createOldConfiguration(false, true, true, false);

        Microservice<Void, ServerFactory> microservice = createNewBuilder().microservice().withFilesystemStorage(folder)
                .withPatch(0, 1, (root, metadata, objectMapper) -> {
                    //mimics a project patch that roundtrips the json through the current factory classes,
                    //which silently drops the removed attribute (unknown property) including its content
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
        Assertions.assertNotNull(root.keptRef.get());
        Assertions.assertEquals("shared-value", root.keptRef.get().name.get());
        microservice.stop();
    }

    @Test
    public void test_definitionRelocatedToRemainingListReference() {
        createOldConfiguration(false, true, false, true);

        Microservice<Void, ServerFactory> microservice = createNewBuilder().microservice().withFilesystemStorage(folder).build();
        microservice.start();
        ServerFactory root = microservice.prepareNewFactory().root;
        Assertions.assertEquals(1, root.keptList.size());
        Assertions.assertEquals("shared-value", root.keptList.get(0).name.get());
        microservice.stop();
    }

    @Test
    public void test_unreferencedDefinitionDropped() {
        createOldConfiguration(false, true, false, false);

        Microservice<Void, ServerFactory> microservice = createNewBuilder().microservice().withFilesystemStorage(folder).build();
        microservice.start();
        ServerFactory root = microservice.prepareNewFactory().root;
        Assertions.assertNull(root.keptRef.get());
        Assertions.assertTrue(root.keptList.isEmpty());
        microservice.stop();
    }

    @Test
    public void test_definitionNestedInRemovedClassRelocated() {
        //the shared factory's first occurrence is nested inside a factory whose class is removed,
        //which itself sits in a removed attribute
        createOldConfiguration(true, false, true, false);

        Microservice<Void, ServerFactory> microservice = createNewBuilder().microservice().withFilesystemStorage(folder).build();
        microservice.start();
        ServerFactory root = microservice.prepareNewFactory().root;
        Assertions.assertNotNull(root.keptRef.get());
        Assertions.assertEquals("shared-value", root.keptRef.get().name.get());
        microservice.stop();
    }
}

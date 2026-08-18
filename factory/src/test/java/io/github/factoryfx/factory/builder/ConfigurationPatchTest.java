package io.github.factoryfx.factory.builder;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.storage.DataUpdate;
import io.github.factoryfx.factory.storage.StoredDataMetadata;
import io.github.factoryfx.server.Microservice;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class ConfigurationPatchTest {

    public static class PatchExampleFactory extends SimpleFactoryBase<Void, PatchExampleFactory> {
        public final StringAttribute stringAttribute = new StringAttribute().nullable();

        @Override
        protected Void createImpl() {
            return null;
        }
    }

    @TempDir
    public Path folder;

    private Microservice<Void, PatchExampleFactory> build(Consumer<MicroserviceBuilder<Void, PatchExampleFactory>> configurator) {
        FactoryTreeBuilder<Void, PatchExampleFactory> builder = new FactoryTreeBuilder<>(PatchExampleFactory.class, ctx -> {
            PatchExampleFactory factory = new PatchExampleFactory();
            factory.stringAttribute.set("initial");
            return factory;
        });
        MicroserviceBuilder<Void, PatchExampleFactory> microserviceBuilder = builder.microservice().withFilesystemStorage(folder);
        configurator.accept(microserviceBuilder);
        return microserviceBuilder.build();
    }

    private void createStoredConfigurations() {
        Microservice<Void, PatchExampleFactory> microservice = build(msb -> {});
        microservice.start();
        DataUpdate<PatchExampleFactory> update = microservice.prepareNewFactory();
        update.root.stringAttribute.set("v1");
        microservice.updateCurrentFactory(update);
        microservice.stop();
    }

    @Test
    public void test_everyTimePatch_appliedToCurrentAndHistory_filesUntouched() throws IOException {
        createStoredConfigurations();
        String currentFactoryFileBefore = Files.readString(folder.resolve("currentFactory.json"));

        Microservice<Void, PatchExampleFactory> microservice = build(msb -> msb.withPatch(
                (root, metadata, objectMapper) -> root.setAttributeValue("stringAttribute", TextNode.valueOf("patched"))));
        microservice.start();
        Assertions.assertEquals("patched", microservice.prepareNewFactory().root.stringAttribute.get());

        for (StoredDataMetadata metadata : microservice.getHistoryFactoryList(true)) {
            Assertions.assertEquals("patched", microservice.getHistoryFactory(metadata.id).stringAttribute.get());
        }
        microservice.stop();

        Assertions.assertEquals(currentFactoryFileBefore, Files.readString(folder.resolve("currentFactory.json")));
    }

    @Test
    public void test_versionedPatches_chainInOrder() {
        createStoredConfigurations();

        Microservice<Void, PatchExampleFactory> microservice = build(msb -> msb
                .withPatch(1, 2, (root, metadata, objectMapper) -> root.setAttributeValue("stringAttribute", TextNode.valueOf(root.getAttributeValue("stringAttribute").asText() + "B")))
                .withPatch(0, 1, (root, metadata, objectMapper) -> root.setAttributeValue("stringAttribute", TextNode.valueOf(root.getAttributeValue("stringAttribute").asText() + "A"))));
        microservice.start();
        Assertions.assertEquals("v1AB", microservice.prepareNewFactory().root.stringAttribute.get());
        microservice.stop();
    }

    @Test
    public void test_savedConfiguration_getsCurrentSchemaVersion() {
        createStoredConfigurations();

        Microservice<Void, PatchExampleFactory> microservice = build(msb -> msb
                .withPatch(0, 1, (root, metadata, objectMapper) -> root.setAttributeValue("stringAttribute", TextNode.valueOf("patched"))));
        microservice.start();
        DataUpdate<PatchExampleFactory> update = microservice.prepareNewFactory();
        update.root.stringAttribute.set("v2");
        microservice.updateCurrentFactory(update);
        String currentId = update.baseVersionId;
        microservice.stop();

        //the newly saved configuration is stored with the current schema version (1), so the 0->1 patch does not run for it
        Microservice<Void, PatchExampleFactory> microservice2 = build(msb -> msb
                .withPatch(0, 1, (root, metadata, objectMapper) -> root.setAttributeValue("stringAttribute", TextNode.valueOf("patched"))));
        microservice2.start();
        Assertions.assertEquals("v2", microservice2.prepareNewFactory().root.stringAttribute.get());
        //old history versions still get patched
        Assertions.assertEquals("patched", microservice2.getHistoryFactory(currentId).stringAttribute.get());
        microservice2.stop();
    }

    @Test
    public void test_persistConfigurationPatches_patchRunsOnceAndIsPersisted() {
        createStoredConfigurations();

        AtomicInteger patchCounter = new AtomicInteger();
        Microservice<Void, PatchExampleFactory> microservice = build(msb -> msb.withPatch(0, 1, (root, metadata, objectMapper) -> {
            patchCounter.incrementAndGet();
            root.setAttributeValue("stringAttribute", TextNode.valueOf("patched"));
        }));
        microservice.persistConfigurationPatches();
        //patchAll visits the current configuration twice (current file + its history copy), the version gating skips repeated visits of already rewritten files:
        //current file (runs), history copy of current (runs), initial history entry (runs), current history entry revisited after rewrite (skipped)
        Assertions.assertEquals(3, patchCounter.get());

        AtomicInteger patchCounterAfterPersist = new AtomicInteger();
        Microservice<Void, PatchExampleFactory> microservice2 = build(msb -> msb.withPatch(0, 1, (root, metadata, objectMapper) -> {
            patchCounterAfterPersist.incrementAndGet();
            root.setAttributeValue("stringAttribute", TextNode.valueOf("patched"));
        }));
        microservice2.start();
        Assertions.assertEquals("patched", microservice2.prepareNewFactory().root.stringAttribute.get());
        for (StoredDataMetadata metadata : microservice2.getHistoryFactoryList(true)) {
            microservice2.getHistoryFactory(metadata.id);
        }
        microservice2.stop();
        Assertions.assertEquals(0, patchCounterAfterPersist.get());
    }

    @Test
    public void test_legacyMetadataWithoutVersion_treatedAsVersionZero() throws IOException {
        createStoredConfigurations();
        removeConfigurationSchemaVersionFromAllMetadataFiles();

        Microservice<Void, PatchExampleFactory> microservice = build(msb -> msb.withPatch(0, 1,
                (root, metadata, objectMapper) -> root.setAttributeValue("stringAttribute", TextNode.valueOf("patched"))));
        microservice.start();
        Assertions.assertEquals("patched", microservice.prepareNewFactory().root.stringAttribute.get());
        microservice.stop();
    }

    @Test
    public void test_newerStoredVersion_versionedPatchesSkipped_everyTimePatchesRun() {
        {
            Microservice<Void, PatchExampleFactory> microservice = build(msb -> msb.withConfigurationSchemaVersion(5));
            microservice.start();
            DataUpdate<PatchExampleFactory> update = microservice.prepareNewFactory();
            update.root.stringAttribute.set("v1");
            microservice.updateCurrentFactory(update);
            microservice.stop();
        }

        AtomicInteger versionedPatchCounter = new AtomicInteger();
        Microservice<Void, PatchExampleFactory> microservice = build(msb -> msb
                .withPatch(0, 1, (root, metadata, objectMapper) -> versionedPatchCounter.incrementAndGet())
                .withPatch((root, metadata, objectMapper) -> root.setAttributeValue("stringAttribute", TextNode.valueOf("everyTime"))));
        microservice.start();
        Assertions.assertEquals("everyTime", microservice.prepareNewFactory().root.stringAttribute.get());
        Assertions.assertEquals(0, versionedPatchCounter.get());
        microservice.stop();
    }

    @Test
    public void test_validatePatches_duplicateFromVersion() {
        Assertions.assertThrows(IllegalStateException.class, () -> build(msb -> msb
                .withPatch(0, 1, (root, metadata, objectMapper) -> {})
                .withPatch(0, 2, (root, metadata, objectMapper) -> {})));
    }

    @Test
    public void test_validatePatches_gapInChain() {
        Assertions.assertThrows(IllegalStateException.class, () -> build(msb -> msb
                .withPatch(0, 1, (root, metadata, objectMapper) -> {})
                .withPatch(2, 3, (root, metadata, objectMapper) -> {})));
    }

    @Test
    public void test_validatePatches_toVersionNotGreaterThanFromVersion() {
        Assertions.assertThrows(IllegalStateException.class, () -> build(msb -> msb
                .withPatch(1, 1, (root, metadata, objectMapper) -> {})));
    }

    @Test
    public void test_validatePatches_declaredVersionLowerThanHighestPatchVersion() {
        Assertions.assertThrows(IllegalStateException.class, () -> build(msb -> msb
                .withConfigurationSchemaVersion(1)
                .withPatch(0, 1, (root, metadata, objectMapper) -> {})
                .withPatch(1, 2, (root, metadata, objectMapper) -> {})));
    }

    private void removeConfigurationSchemaVersionFromAllMetadataFiles() throws IOException {
        List<Path> metadataFiles = new ArrayList<>();
        metadataFiles.add(folder.resolve("currentFactory_metadata.json"));
        try (Stream<Path> files = Files.walk(folder.resolve("history")).filter(Files::isRegularFile)) {
            files.filter(path -> path.toString().endsWith("_metadata.json")).forEach(metadataFiles::add);
        }
        for (Path metadataFile : metadataFiles) {
            ObjectNode metadata = (ObjectNode) ObjectMapperBuilder.build().readTree(Files.readString(metadataFile));
            metadata.remove("configurationSchemaVersion");
            Files.writeString(metadataFile, ObjectMapperBuilder.build().writeValueAsString(metadata));
        }
    }
}

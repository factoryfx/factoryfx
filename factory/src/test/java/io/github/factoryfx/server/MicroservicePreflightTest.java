package io.github.factoryfx.server;

import com.fasterxml.jackson.databind.node.TextNode;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.builder.MicroserviceBuilder;
import io.github.factoryfx.factory.storage.DataUpdate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class MicroservicePreflightTest {

    public static class PreflightExampleFactory extends SimpleFactoryBase<Void, PreflightExampleFactory> {
        public final StringAttribute stringAttribute = new StringAttribute().nullable();

        @Override
        protected Void createImpl() {
            return null;
        }
    }

    @TempDir
    public Path folder;

    @TempDir
    public Path snapshotFolder;

    private Microservice<Void, PreflightExampleFactory> build(Consumer<MicroserviceBuilder<Void, PreflightExampleFactory>> configurator) {
        FactoryTreeBuilder<Void, PreflightExampleFactory> builder = new FactoryTreeBuilder<>(PreflightExampleFactory.class, ctx -> {
            PreflightExampleFactory factory = new PreflightExampleFactory();
            factory.stringAttribute.set("initial");
            return factory;
        });
        MicroserviceBuilder<Void, PreflightExampleFactory> microserviceBuilder = builder.microservice().withFilesystemStorage(folder);
        configurator.accept(microserviceBuilder);
        return microserviceBuilder.build();
    }

    private void createStoredConfiguration(String value) {
        Microservice<Void, PreflightExampleFactory> microservice = build(msb -> {});
        microservice.start();
        DataUpdate<PreflightExampleFactory> update = microservice.prepareNewFactory();
        update.root.stringAttribute.set(value);
        microservice.updateCurrentFactory(update);
        microservice.stop();
    }

    @Test
    public void test_preflight_ok() {
        createStoredConfiguration("v1");

        Microservice<Void, PreflightExampleFactory> microservice = build(msb -> msb.withPatch(
                (root, metadata, objectMapper) -> root.setAttributeValue("stringAttribute", TextNode.valueOf("patched"))));
        PreflightCheckReport report = microservice.preflightCheck(true);
        Assertions.assertTrue(report.isOk(), report.report());
    }

    @Test
    public void test_preflight_reportsBrokenCurrentConfiguration_insteadOfThrowing() throws IOException {
        createStoredConfiguration("v1");
        Files.writeString(folder.resolve("currentFactory.json"), "garbage - not json");

        Microservice<Void, PreflightExampleFactory> microservice = build(msb -> {});
        PreflightCheckReport report = microservice.preflightCheck();
        Assertions.assertFalse(report.isOk());
        Assertions.assertTrue(report.problems.get(0).contains("can't load the current configuration"), report.report());
    }

    @Test
    public void test_preflight_reportsBrokenPatch() {
        createStoredConfiguration("v1");

        Microservice<Void, PreflightExampleFactory> microservice = build(msb -> msb.withPatch((root, metadata, objectMapper) -> {
            throw new IllegalStateException("patch is broken");
        }));
        PreflightCheckReport report = microservice.preflightCheck();
        Assertions.assertFalse(report.isOk());
        Assertions.assertTrue(report.problems.get(0).contains("patch is broken"), report.report());
    }

    @Test
    public void test_preflight_historyOnlyCheckedWhenRequested() throws IOException {
        createStoredConfiguration("v1");
        //corrupt one history file, the current configuration stays intact
        try (Stream<Path> files = Files.walk(folder.resolve("history")).filter(Files::isRegularFile)) {
            Path historyFile = files.filter(path -> !path.toString().endsWith("_metadata.json")).findFirst().orElseThrow();
            Files.writeString(historyFile, "garbage - not json");
        }

        Microservice<Void, PreflightExampleFactory> microservice = build(msb -> {});
        Assertions.assertTrue(microservice.preflightCheck(false).isOk());

        PreflightCheckReport reportWithHistory = microservice.preflightCheck(true);
        Assertions.assertFalse(reportWithHistory.isOk());
        Assertions.assertTrue(reportWithHistory.problems.get(0).contains("can't load the history configuration"), reportWithHistory.report());
    }

    @Test
    public void test_snapshot_containsRawUnpatchedData() throws IOException {
        createStoredConfiguration("v1");

        Microservice<Void, PreflightExampleFactory> microservice = build(msb -> msb.withPatch(
                (root, metadata, objectMapper) -> root.setAttributeValue("stringAttribute", TextNode.valueOf("patched"))));
        Path snapshot = snapshotFolder.resolve("snapshot.json");
        microservice.saveConfigurationSnapshot(snapshot);

        String snapshotContent = Files.readString(snapshot);
        Assertions.assertTrue(snapshotContent.contains("v1"), snapshotContent);
        Assertions.assertFalse(snapshotContent.contains("patched"), snapshotContent);
    }

    @Test
    public void test_snapshot_restore_notStarted_createsNewVersion() {
        createStoredConfiguration("v1");

        Path snapshot = snapshotFolder.resolve("snapshot.json");
        {
            Microservice<Void, PreflightExampleFactory> microservice = build(msb -> {});
            microservice.saveConfigurationSnapshot(snapshot);

            //change the configuration after the snapshot
            microservice.start();
            DataUpdate<PreflightExampleFactory> update = microservice.prepareNewFactory();
            update.root.stringAttribute.set("v2");
            microservice.updateCurrentFactory(update);
            microservice.stop();
        }

        Microservice<Void, PreflightExampleFactory> microservice = build(msb -> {});
        int historySizeBefore = microservice.getHistoryFactoryList(true).size();
        Assertions.assertNull(microservice.loadConfigurationSnapshot(snapshot));
        Assertions.assertEquals(historySizeBefore + 1, microservice.getHistoryFactoryList(true).size());

        microservice.start();
        Assertions.assertEquals("v1", microservice.prepareNewFactory().root.stringAttribute.get());
        microservice.stop();
    }

    @Test
    public void test_snapshot_restore_started_updatesRunningApplication() {
        createStoredConfiguration("v1");

        Path snapshot = snapshotFolder.resolve("snapshot.json");
        Microservice<Void, PreflightExampleFactory> microservice = build(msb -> {});
        microservice.saveConfigurationSnapshot(snapshot);
        microservice.start();

        DataUpdate<PreflightExampleFactory> update = microservice.prepareNewFactory();
        update.root.stringAttribute.set("v2");
        microservice.updateCurrentFactory(update);
        Assertions.assertEquals("v2", microservice.prepareNewFactory().root.stringAttribute.get());

        Assertions.assertNotNull(microservice.loadConfigurationSnapshot(snapshot));
        Assertions.assertEquals("v1", microservice.prepareNewFactory().root.stringAttribute.get());
        microservice.stop();
    }

    @Test
    public void test_snapshot_restoreRaw_storesUnpatchedData_patchesStillApplyOnLoad() throws IOException {
        createStoredConfiguration("v1");

        Path snapshot = snapshotFolder.resolve("snapshot.json");
        build(msb -> {}).saveConfigurationSnapshot(snapshot);

        Microservice<Void, PreflightExampleFactory> microservice = build(msb -> msb.withPatch(0, 1,
                (root, metadata, objectMapper) -> root.setAttributeValue("stringAttribute", TextNode.valueOf("patched"))));
        int historySizeBefore = microservice.getHistoryFactoryList(true).size();
        Assertions.assertNull(microservice.loadConfigurationSnapshotRaw(snapshot));
        Assertions.assertEquals(historySizeBefore + 1, microservice.getHistoryFactoryList(true).size());

        //the stored form stays raw
        String storedCurrent = Files.readString(folder.resolve("currentFactory.json"));
        Assertions.assertTrue(storedCurrent.contains("v1"), storedCurrent);
        Assertions.assertFalse(storedCurrent.contains("patched"), storedCurrent);

        //the version-gated patch still applies on load because the schema version of the snapshot is preserved
        microservice.start();
        Assertions.assertEquals("patched", microservice.prepareNewFactory().root.stringAttribute.get());
        microservice.stop();
    }

    @Test
    public void test_snapshot_restoreRaw_started_updatesRunningApplication() throws IOException {
        createStoredConfiguration("v1");

        Path snapshot = snapshotFolder.resolve("snapshot.json");
        Microservice<Void, PreflightExampleFactory> microservice = build(msb -> {});
        microservice.saveConfigurationSnapshot(snapshot);
        microservice.start();

        DataUpdate<PreflightExampleFactory> update = microservice.prepareNewFactory();
        update.root.stringAttribute.set("v2");
        microservice.updateCurrentFactory(update);

        Assertions.assertNotNull(microservice.loadConfigurationSnapshotRaw(snapshot));
        Assertions.assertEquals("v1", microservice.prepareNewFactory().root.stringAttribute.get());

        String storedCurrent = Files.readString(folder.resolve("currentFactory.json"));
        Assertions.assertTrue(storedCurrent.contains("v1"), storedCurrent);
        microservice.stop();
    }

    @Test
    public void test_snapshot_restore_appliesPatches() {
        createStoredConfiguration("v1");

        Path snapshot = snapshotFolder.resolve("snapshot.json");
        build(msb -> {}).saveConfigurationSnapshot(snapshot);

        Microservice<Void, PreflightExampleFactory> microservice = build(msb -> msb.withPatch(
                (root, metadata, objectMapper) -> root.setAttributeValue("stringAttribute", TextNode.valueOf("patched"))));
        microservice.loadConfigurationSnapshot(snapshot);
        microservice.start();
        Assertions.assertEquals("patched", microservice.prepareNewFactory().root.stringAttribute.get());
        microservice.stop();
    }
}

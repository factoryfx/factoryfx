package io.github.factoryfx.server;

import com.fasterxml.jackson.databind.node.TextNode;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.builder.MicroserviceBuilder;
import io.github.factoryfx.factory.storage.DataUpdate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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

    public static class LifecycleExampleLiveObject {
    }

    public static class LifecycleExampleFactory extends SimpleFactoryBase<LifecycleExampleLiveObject, LifecycleExampleFactory> {
        public static int createCount;
        public static int startCount;
        public static boolean failCreate;

        public final StringAttribute stringAttribute = new StringAttribute().nullable();

        public LifecycleExampleFactory() {
            configLifeCycle().setStarter(liveObject -> startCount++);
        }

        @Override
        protected LifecycleExampleLiveObject createImpl() {
            if (failCreate) {
                throw new IllegalStateException("create is broken");
            }
            createCount++;
            return new LifecycleExampleLiveObject();
        }
    }

    @TempDir
    public Path folder;

    @TempDir
    public Path snapshotFolder;

    @BeforeEach
    public void resetLifecycleCounters() {
        LifecycleExampleFactory.createCount = 0;
        LifecycleExampleFactory.startCount = 0;
        LifecycleExampleFactory.failCreate = false;
    }

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
        PreflightCheckReport report = microservice.deployment().preflightCheck(new PreflightCheckOptions().includeHistory());
        Assertions.assertTrue(report.isOk(), report.report());
    }

    @Test
    public void test_preflight_reportsBrokenCurrentConfiguration_insteadOfThrowing() throws IOException {
        createStoredConfiguration("v1");
        Files.writeString(folder.resolve("currentFactory.json"), "garbage - not json");

        Microservice<Void, PreflightExampleFactory> microservice = build(msb -> {});
        PreflightCheckReport report = microservice.deployment().preflightCheck();
        Assertions.assertFalse(report.isOk());
        Assertions.assertTrue(report.problems.get(0).contains("can't load the current configuration"), report.report());
    }

    @Test
    public void test_preflight_reportsBrokenPatch() {
        createStoredConfiguration("v1");

        Microservice<Void, PreflightExampleFactory> microservice = build(msb -> msb.withPatch((root, metadata, objectMapper) -> {
            throw new IllegalStateException("patch is broken");
        }));
        PreflightCheckReport report = microservice.deployment().preflightCheck();
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
        Assertions.assertTrue(microservice.deployment().preflightCheck().isOk());

        PreflightCheckReport reportWithHistory = microservice.deployment().preflightCheck(new PreflightCheckOptions().includeHistory());
        Assertions.assertFalse(reportWithHistory.isOk());
        Assertions.assertTrue(reportWithHistory.problems.get(0).contains("can't load the history configuration"), reportWithHistory.report());
    }

    private Microservice<LifecycleExampleLiveObject, LifecycleExampleFactory> buildLifecycle() {
        FactoryTreeBuilder<LifecycleExampleLiveObject, LifecycleExampleFactory> builder = new FactoryTreeBuilder<>(LifecycleExampleFactory.class, ctx -> {
            LifecycleExampleFactory factory = new LifecycleExampleFactory();
            factory.stringAttribute.set("initial");
            return factory;
        });
        return builder.microservice().withFilesystemStorage(folder).build();
    }

    private void createStoredLifecycleConfiguration() {
        Microservice<LifecycleExampleLiveObject, LifecycleExampleFactory> microservice = buildLifecycle();
        microservice.start();
        microservice.stop();
        LifecycleExampleFactory.createCount = 0;
        LifecycleExampleFactory.startCount = 0;
    }

    public static class ServerValidatedPreflightFactory extends SimpleFactoryBase<Void, ServerValidatedPreflightFactory> {
        public final StringAttribute stringAttribute = new StringAttribute().nullable()
                .serverValidation(value -> new io.github.factoryfx.factory.validation.ValidationResult("serverInvalid".equals(value),
                        new io.github.factoryfx.factory.util.LanguageText("server error")));

        @Override
        protected Void createImpl() {
            return null;
        }
    }

    private Microservice<Void, ServerValidatedPreflightFactory> buildServerValidated() {
        FactoryTreeBuilder<Void, ServerValidatedPreflightFactory> builder = new FactoryTreeBuilder<>(ServerValidatedPreflightFactory.class, ctx -> new ServerValidatedPreflightFactory());
        return builder.microservice().withFilesystemStorage(folder).build();
    }

    @Test
    public void test_preflight_reportsServerValidationErrors() {
        Microservice<Void, ServerValidatedPreflightFactory> setup = buildServerValidated();
        setup.start();
        //the in-JVM programmatic self-update is not server-validated, it can persist an invalid value
        setup.update((root, idToFactory) -> root.stringAttribute.set("serverInvalid"));
        setup.stop();

        Microservice<Void, ServerValidatedPreflightFactory> microservice = buildServerValidated();
        PreflightCheckReport report = microservice.deployment().preflightCheck();
        Assertions.assertFalse(report.isOk());
        Assertions.assertTrue(report.problems.get(0).contains("server validation error"), report.report());
        Assertions.assertTrue(report.problems.get(0).contains("server error"), report.report());
    }

    @Test
    public void test_preflight_createLiveObjects_createsButNeverStarts() {
        createStoredLifecycleConfiguration();

        Microservice<LifecycleExampleLiveObject, LifecycleExampleFactory> microservice = buildLifecycle();
        PreflightCheckReport report = microservice.deployment().preflightCheck(new PreflightCheckOptions().createLiveObjects());
        Assertions.assertTrue(report.isOk(), report.report());
        Assertions.assertEquals(1, LifecycleExampleFactory.createCount);
        Assertions.assertEquals(0, LifecycleExampleFactory.startCount);

        //the fixture detects a real start
        microservice.start();
        Assertions.assertEquals(1, LifecycleExampleFactory.startCount);
        microservice.stop();
    }

    @Test
    public void test_preflight_withoutCreateLiveObjectsOption_doesNotCreate() {
        createStoredLifecycleConfiguration();

        Microservice<LifecycleExampleLiveObject, LifecycleExampleFactory> microservice = buildLifecycle();
        Assertions.assertTrue(microservice.deployment().preflightCheck().isOk());
        Assertions.assertTrue(microservice.deployment().preflightCheck(new PreflightCheckOptions().includeHistory()).isOk());
        Assertions.assertEquals(0, LifecycleExampleFactory.createCount);
        Assertions.assertEquals(0, LifecycleExampleFactory.startCount);
    }

    @Test
    public void test_preflight_createLiveObjects_reportsCreateFailure_insteadOfThrowing() {
        createStoredLifecycleConfiguration();
        LifecycleExampleFactory.failCreate = true;

        Microservice<LifecycleExampleLiveObject, LifecycleExampleFactory> microservice = buildLifecycle();
        PreflightCheckReport report = microservice.deployment().preflightCheck(new PreflightCheckOptions().createLiveObjects());
        Assertions.assertFalse(report.isOk());
        Assertions.assertTrue(report.problems.get(0).contains("liveObject creation failed"), report.report());
        Assertions.assertTrue(report.problems.get(0).contains("LifecycleExampleFactory"), report.report());
        Assertions.assertTrue(report.problems.get(0).contains("create is broken"), report.report());
        Assertions.assertEquals(0, LifecycleExampleFactory.startCount);
    }

    @Test
    public void test_snapshot_containsRawUnpatchedData() throws IOException {
        createStoredConfiguration("v1");

        Microservice<Void, PreflightExampleFactory> microservice = build(msb -> msb.withPatch(
                (root, metadata, objectMapper) -> root.setAttributeValue("stringAttribute", TextNode.valueOf("patched"))));
        Path snapshot = snapshotFolder.resolve("snapshot.json");
        microservice.deployment().saveConfigurationSnapshot(snapshot);

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
            microservice.deployment().saveConfigurationSnapshot(snapshot);

            //change the configuration after the snapshot
            microservice.start();
            DataUpdate<PreflightExampleFactory> update = microservice.prepareNewFactory();
            update.root.stringAttribute.set("v2");
            microservice.updateCurrentFactory(update);
            microservice.stop();
        }

        Microservice<Void, PreflightExampleFactory> microservice = build(msb -> {});
        int historySizeBefore = microservice.getHistoryFactoryList(true).size();
        Assertions.assertNull(microservice.deployment().loadConfigurationSnapshot(snapshot));
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
        microservice.deployment().saveConfigurationSnapshot(snapshot);
        microservice.start();

        DataUpdate<PreflightExampleFactory> update = microservice.prepareNewFactory();
        update.root.stringAttribute.set("v2");
        microservice.updateCurrentFactory(update);
        Assertions.assertEquals("v2", microservice.prepareNewFactory().root.stringAttribute.get());

        Assertions.assertNotNull(microservice.deployment().loadConfigurationSnapshot(snapshot));
        Assertions.assertEquals("v1", microservice.prepareNewFactory().root.stringAttribute.get());
        microservice.stop();
    }

    @Test
    public void test_snapshot_restoreRaw_storesUnpatchedData_patchesStillApplyOnLoad() throws IOException {
        createStoredConfiguration("v1");

        Path snapshot = snapshotFolder.resolve("snapshot.json");
        build(msb -> {}).deployment().saveConfigurationSnapshot(snapshot);

        Microservice<Void, PreflightExampleFactory> microservice = build(msb -> msb.withPatch(0, 1,
                (root, metadata, objectMapper) -> root.setAttributeValue("stringAttribute", TextNode.valueOf("patched"))));
        int historySizeBefore = microservice.getHistoryFactoryList(true).size();
        Assertions.assertNull(microservice.deployment().loadConfigurationSnapshotRaw(snapshot));
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
        microservice.deployment().saveConfigurationSnapshot(snapshot);
        microservice.start();

        DataUpdate<PreflightExampleFactory> update = microservice.prepareNewFactory();
        update.root.stringAttribute.set("v2");
        microservice.updateCurrentFactory(update);

        Assertions.assertNotNull(microservice.deployment().loadConfigurationSnapshotRaw(snapshot));
        Assertions.assertEquals("v1", microservice.prepareNewFactory().root.stringAttribute.get());

        String storedCurrent = Files.readString(folder.resolve("currentFactory.json"));
        Assertions.assertTrue(storedCurrent.contains("v1"), storedCurrent);
        microservice.stop();
    }

    @Test
    public void test_snapshot_restore_appliesPatches() {
        createStoredConfiguration("v1");

        Path snapshot = snapshotFolder.resolve("snapshot.json");
        build(msb -> {}).deployment().saveConfigurationSnapshot(snapshot);

        Microservice<Void, PreflightExampleFactory> microservice = build(msb -> msb.withPatch(
                (root, metadata, objectMapper) -> root.setAttributeValue("stringAttribute", TextNode.valueOf("patched"))));
        microservice.deployment().loadConfigurationSnapshot(snapshot);
        microservice.start();
        Assertions.assertEquals("patched", microservice.prepareNewFactory().root.stringAttribute.get());
        microservice.stop();
    }
}

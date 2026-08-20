package io.github.factoryfx.server;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.log.FactoryUpdateLog;
import io.github.factoryfx.factory.merge.MergeDiffInfo;
import io.github.factoryfx.factory.storage.DataUpdate;
import io.github.factoryfx.factory.util.LanguageText;
import io.github.factoryfx.factory.validation.ValidationResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

public class MicroserviceServerValidationTest {

    public static class ServerValidatedRootFactory extends SimpleFactoryBase<Void, ServerValidatedRootFactory> {
        public final StringAttribute stringAttribute = new StringAttribute().nullable()
                .validation(value -> new ValidationResult("clientInvalid".equals(value), new LanguageText("client error")))
                .serverValidation(value -> new ValidationResult("serverInvalid".equals(value), new LanguageText("server error")));

        @Override
        protected Void createImpl() {
            return null;
        }
    }

    @TempDir
    public Path folder;

    private Microservice<Void, ServerValidatedRootFactory> build() {
        FactoryTreeBuilder<Void, ServerValidatedRootFactory> builder = new FactoryTreeBuilder<>(ServerValidatedRootFactory.class, ctx -> {
            ServerValidatedRootFactory factory = new ServerValidatedRootFactory();
            factory.stringAttribute.set("initial");
            return factory;
        });
        return builder.microservice().withFilesystemStorage(folder).build();
    }

    @Test
    public void test_update_rejectedByServerValidation_nothingAppliedOrPersisted() {
        Microservice<Void, ServerValidatedRootFactory> microservice = build();
        microservice.start();
        int historySizeBefore = microservice.getHistoryFactoryList(true).size();

        DataUpdate<ServerValidatedRootFactory> update = microservice.prepareNewFactory();
        update.root.stringAttribute.set("serverInvalid");
        FactoryUpdateLog<ServerValidatedRootFactory> log = microservice.updateCurrentFactory(update);

        Assertions.assertTrue(log.failedValidation());
        Assertions.assertFalse(log.failedUpdate());
        Assertions.assertFalse(log.successfullyMerged());
        Assertions.assertTrue(log.validationErrors.get(0).contains("server error"), log.validationErrors.get(0));
        //nothing applied, nothing persisted
        Assertions.assertEquals("initial", microservice.prepareNewFactory().root.stringAttribute.get());
        Assertions.assertEquals(historySizeBefore, microservice.getHistoryFactoryList(true).size());
        microservice.stop();
    }

    @Test
    public void test_update_validConfiguration_succeeds() {
        Microservice<Void, ServerValidatedRootFactory> microservice = build();
        microservice.start();

        DataUpdate<ServerValidatedRootFactory> update = microservice.prepareNewFactory();
        update.root.stringAttribute.set("valid");
        FactoryUpdateLog<ServerValidatedRootFactory> log = microservice.updateCurrentFactory(update);

        Assertions.assertFalse(log.failedValidation());
        Assertions.assertTrue(log.successfullyMerged());
        Assertions.assertEquals("valid", microservice.prepareNewFactory().root.stringAttribute.get());
        microservice.stop();
    }

    @Test
    public void test_update_failingClientValidation_doesNotBlockSave() {
        Microservice<Void, ServerValidatedRootFactory> microservice = build();
        microservice.start();

        DataUpdate<ServerValidatedRootFactory> update = microservice.prepareNewFactory();
        update.root.stringAttribute.set("clientInvalid");
        FactoryUpdateLog<ServerValidatedRootFactory> log = microservice.updateCurrentFactory(update);

        Assertions.assertFalse(log.failedValidation());
        Assertions.assertTrue(log.successfullyMerged());
        Assertions.assertEquals("clientInvalid", microservice.prepareNewFactory().root.stringAttribute.get());
        microservice.stop();
    }

    @Test
    public void test_simulateUpdate_reportsServerValidationErrors_withoutRejecting() {
        Microservice<Void, ServerValidatedRootFactory> microservice = build();
        microservice.start();

        DataUpdate<ServerValidatedRootFactory> update = microservice.prepareNewFactory();
        update.root.stringAttribute.set("serverInvalid");
        MergeDiffInfo<ServerValidatedRootFactory> diff = microservice.simulateUpdateCurrentFactory(update);

        Assertions.assertTrue(diff.hasValidationErrors());
        Assertions.assertTrue(diff.validationErrors.get(0).contains("server error"), diff.validationErrors.get(0));
        Assertions.assertTrue(diff.successfullyMerged());//merge semantics unaffected
        Assertions.assertFalse(diff.mergeInfos.isEmpty());
        //simulate persisted nothing
        Assertions.assertEquals("initial", microservice.prepareNewFactory().root.stringAttribute.get());
        microservice.stop();
    }

    @Test
    public void test_revertTo_invalidHistoryConfiguration_rejected() {
        Microservice<Void, ServerValidatedRootFactory> microservice = build();
        microservice.start();

        //the in-JVM programmatic self-update is trusted and NOT server-validated, it can persist an invalid value
        microservice.update((root, idToFactory) -> root.stringAttribute.set("serverInvalid"));
        Assertions.assertEquals("serverInvalid", microservice.prepareNewFactory().root.stringAttribute.get());

        DataUpdate<ServerValidatedRootFactory> update = microservice.prepareNewFactory();
        update.root.stringAttribute.set("valid");
        Assertions.assertTrue(microservice.updateCurrentFactory(update).successfullyMerged());

        //reverting to the invalid version is rejected by server validation
        var invalidHistory = microservice.getHistoryFactoryList(true).stream()
                .filter(m -> "serverInvalid".equals(microservice.getHistoryFactory(m.id).stringAttribute.get()))
                .findFirst().orElseThrow();
        FactoryUpdateLog<ServerValidatedRootFactory> log = microservice.revertTo(invalidHistory, "user");
        Assertions.assertTrue(log.failedValidation());
        Assertions.assertEquals("valid", microservice.prepareNewFactory().root.stringAttribute.get());
        microservice.stop();
    }
}

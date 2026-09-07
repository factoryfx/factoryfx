package io.github.factoryfx.server;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.FactoryManager;
import io.github.factoryfx.factory.RootFactoryWrapper;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.log.FactoryUpdateLog;
import io.github.factoryfx.factory.merge.DataMerger;
import io.github.factoryfx.factory.merge.MergeDiffInfo;
import io.github.factoryfx.factory.storage.DataStorage;
import io.github.factoryfx.factory.storage.DataUpdate;
import io.github.factoryfx.factory.storage.RawFactoryDataAndMetadata;
import io.github.factoryfx.factory.storage.StoredDataMetadata;
import io.github.factoryfx.factory.storage.UpdateSummary;
import io.github.factoryfx.factory.storage.migration.MigrationManager;
import io.github.factoryfx.factory.validation.ValidationError;

/**
 * deployment tooling around a {@link Microservice}, accessed via {@link Microservice#deployment()}: verify an upgrade
 * before switching to a new software version (preflight check), configuration snapshots as a rollback safety net and
 * explicit persistence of the registered configuration patches. intended for deployment scripts/tooling, not for the
 * running application.<br>
 * all methods synchronize on the microservice instance, so they are mutually exclusive with
 * {@link Microservice#start()} and configuration updates.
 *
 * @param <L> Root liveobject
 * @param <R> Root
 */
public class MicroserviceDeployment<L, R extends FactoryBase<L, R>> {

    private final Microservice<L, R> microservice;
    private final FactoryManager<L, R> factoryManager;
    private final DataStorage<R> dataStorage;
    private final FactoryTreeBuilder<L, R> factoryTreeBuilder;
    private final MigrationManager<R> migrationManager;

    MicroserviceDeployment(Microservice<L, R> microservice, FactoryManager<L, R> factoryManager, DataStorage<R> dataStorage, FactoryTreeBuilder<L, R> factoryTreeBuilder, MigrationManager<R> migrationManager) {
        this.microservice = microservice;
        this.factoryManager = factoryManager;
        this.dataStorage = dataStorage;
        this.factoryTreeBuilder = factoryTreeBuilder;
        this.migrationManager = migrationManager;
    }

    /**
     * persist the configuration patches registered on the builder: applies them to all stored configurations
     * (current and history) and writes the results including the bumped configuration schema version back to the storage.<br>
     * afterwards version-gated patches no longer run on load for the patched configurations.
     */
    public void persistConfigurationPatches() {
        synchronized (microservice) {
            checkMigrationManagerAvailable();
            dataStorage.patchAll(migrationManager.createConfigurationPatcher());
        }
    }

    /**
     * preflight check for the current configuration, see {@link #preflightCheck(PreflightCheckOptions)}
     * @return report
     */
    public PreflightCheckReport preflightCheck() {
        return preflightCheck(new PreflightCheckOptions());
    }

    /**
     * check that this software version (factory classes, registered patches and migrations) can work with the
     * stored configuration, WITHOUT starting the application. intended for deployment tooling before switching to a
     * new software version.<br>
     * checks: the current configuration deserializes through the regular load path (patches, migrations, json binding),
     * the resulting factory tree has no validation errors (client and server validations) and the treeBuilder rebuild
     * succeeds.
     * {@link PreflightCheckOptions#createLiveObjects()} additionally creates all liveObjects without starting them.
     * {@link PreflightCheckOptions#includeHistory()} additionally validates the past configurations.
     *
     * @param options options
     * @return report with all found problems, {@link PreflightCheckReport#isOk()} when safe to start
     */
    public PreflightCheckReport preflightCheck(PreflightCheckOptions options) {
        synchronized (microservice) {
            List<String> problems = new ArrayList<>();
            R currentRoot = null;
            try {
                currentRoot = dataStorage.getCurrentData().root;
            } catch (RuntimeException e) {
                problems.add("can't load the current configuration (patches/migrations/deserialization failed): " + exceptionSummary(e));
            }
            if (currentRoot != null) {
                for (FactoryBase<?, R> factory : currentRoot.internal().collectChildrenDeep()) {
                    for (ValidationError validationError : factory.internal().validateFlat()) {
                        problems.add("validation error:\n" + validationError.getSimpleErrorDescription());
                    }
                    for (ValidationError validationError : factory.internal().validateFlatServer()) {
                        problems.add("server validation error:\n" + validationError.getSimpleErrorDescription());
                    }
                }
                if (options.createLiveObjects) {
                    currentRoot.internal().finalise();
                    currentRoot.internal().setFactoryTreeBuilder(factoryTreeBuilder);
                }
                if (factoryTreeBuilder.isPersistentFactoryBuilder() && (currentRoot.internal().getTreeBuilderName() != null || currentRoot.internal().isTreeBuilderClassUsed())) {
                    try {
                        R rebuildRoot = factoryTreeBuilder.rebuildTreeForExistingConfiguration(currentRoot);
                        if (options.createLiveObjects) {
                            //merge like start() so creation runs on the tree start() would use, nothing is persisted
                            MergeDiffInfo<R> mergeDiffInfo = new DataMerger<>(currentRoot, currentRoot.utility().copy(), rebuildRoot).createMergeResult((p) -> true, true).executeMerge();
                            if (!mergeDiffInfo.successfullyMerged()) {
                                problems.add("can't apply changes from FactoryTreeBuilder to the current configuration");
                            }
                        }
                    } catch (RuntimeException e) {
                        problems.add("treeBuilder rebuild for the current configuration failed: " + exceptionSummary(e));
                    }
                }
                if (options.createLiveObjects) {
                    createLiveObjects(currentRoot, problems);
                }
            }
            if (options.includeHistory) {
                for (StoredDataMetadata metadata : dataStorage.getHistoryDataList(true)) {
                    try {
                        dataStorage.getHistoryData(metadata.id);
                    } catch (RuntimeException e) {
                        problems.add("can't load the history configuration " + metadata.id + ": " + exceptionSummary(e));
                    }
                }
            }
            return new PreflightCheckReport(problems);
        }
    }

    /**
     * create all liveObjects like the first pass of {@link FactoryManager#start(RootFactoryWrapper)} but never start
     * them. the created objects are discarded (per the lifecycle contract external resources are claimed in start,
     * so created-but-never-started liveObjects hold none and no destroy is needed).
     */
    private void createLiveObjects(R currentRoot, List<String> problems) {
        currentRoot.internal().setMicroservice(microservice);
        RootFactoryWrapper<R> rootFactoryWrapper;
        try {
            rootFactoryWrapper = new RootFactoryWrapper<>(currentRoot);
        } catch (RuntimeException e) {
            problems.add("liveObject creation check failed: " + exceptionSummary(e));
            return;
        }
        for (FactoryBase<?, R> factory : rootFactoryWrapper.getFactoriesInCreateAndStartOrder()) {
            try {
                factory.internal().instance();
            } catch (RuntimeException e) {
                problems.add("liveObject creation failed for " + factory.internal().getFactoryDisplayText() + ": " + exceptionSummary(e));
                break;//dependent factories would only fail as a consequence of the first failure
            }
        }
    }

    /**
     * save a snapshot of the current configuration to a file, as stored (raw json, no patches or migrations applied).<br>
     * because the stored form is unchanged the snapshot can also be restored by an OLDER software version, making it a
     * rollback safety net before an upgrade: save a snapshot, then start the new version.
     *
     * @param target target file
     */
    public void saveConfigurationSnapshot(Path target) {
        synchronized (microservice) {
            checkMigrationManagerAvailable();
            RawFactoryDataAndMetadata raw = dataStorage.getCurrentDataRaw();
            try {
                Files.writeString(target, migrationManager.writeRawFactoryDataAndMetadata(raw));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    /**
     * restore a configuration snapshot created with {@link #saveConfigurationSnapshot(Path)}: the snapshot is loaded
     * through the regular load path (patches and migrations apply) and stored as a NEW configuration version, the
     * existing history is preserved. if the microservice is started the running application is updated as well.<br>
     * the patched result is persisted, see {@link #loadConfigurationSnapshotRaw(Path)} to store the snapshot unchanged.<br>
     * while the microservice is NOT started the snapshot is stored without running server validations &ndash; the
     * rollback safety net must not be blockable, run the preflight check before starting.
     *
     * @param snapshot snapshot file
     * @return update log when the microservice is started, null otherwise
     */
    public FactoryUpdateLog<R> loadConfigurationSnapshot(Path snapshot) {
        synchronized (microservice) {
            checkMigrationManagerAvailable();
            RawFactoryDataAndMetadata raw = readSnapshot(snapshot);
            R root = migrationManager.read(raw.root, raw.metadata);
            DataUpdate<R> update = new DataUpdate<>(root, "System", "restore configuration snapshot", dataStorage.getCurrentDataId());
            if (factoryManager.isStarted()) {
                return microservice.updateCurrentFactory(update);
            }
            dataStorage.updateCurrentData(update, null);
            return null;
        }
    }

    /**
     * restore a configuration snapshot created with {@link #saveConfigurationSnapshot(Path)} WITHOUT baking the
     * registered patches and migrations into the stored form: the snapshot json and its configuration schema version
     * are stored unchanged as a NEW configuration version, the existing history is preserved. patches keep applying
     * on load as usual and the stored configuration remains readable by an older software version. if the microservice
     * is started the running application is updated as well (loaded through the regular load path, patches apply
     * in-memory).<br>
     * while the microservice is NOT started the snapshot is stored without running server validations &ndash; the
     * rollback safety net must not be blockable, run the preflight check before starting.
     *
     * @param snapshot snapshot file
     * @return update log when the microservice is started, null otherwise
     */
    public FactoryUpdateLog<R> loadConfigurationSnapshotRaw(Path snapshot) {
        synchronized (microservice) {
            checkMigrationManagerAvailable();
            RawFactoryDataAndMetadata raw = readSnapshot(snapshot);

            FactoryUpdateLog<R> factoryLog = null;
            UpdateSummary changeSummary = null;
            if (factoryManager.isStarted()) {
                R root = migrationManager.read(raw.root, raw.metadata);
                List<String> validationErrors = microservice.validateServer(root);
                if (!validationErrors.isEmpty()) {
                    return FactoryUpdateLog.validationFailed(validationErrors);
                }
                //update the running application without persisting the patched tree, the snapshot is stored raw below
                factoryLog = factoryManager.update(factoryManager.getCurrentFactory().utility().copy(), root, (permission) -> true, true);
                if (factoryLog.failedUpdate() || !factoryLog.successfullyMerged()) {
                    return factoryLog;
                }
                if (factoryLog.mergeDiffInfo != null) {
                    changeSummary = new UpdateSummary(factoryLog.mergeDiffInfo.mergeInfos);
                }
            }

            String currentId = dataStorage.getCurrentDataId();
            RawFactoryDataAndMetadata rawUpdate = new RawFactoryDataAndMetadata();
            rawUpdate.root = raw.root;
            rawUpdate.metadata = new StoredDataMetadata(
                    UUID.randomUUID().toString(),
                    "System",
                    "restore configuration snapshot (raw)",
                    currentId,
                    changeSummary,
                    raw.metadata.dataStorageMetadataDictionary,
                    currentId,
                    raw.metadata.configurationSchemaVersion);
            dataStorage.updateCurrentDataRaw(rawUpdate);
            return factoryLog;
        }
    }

    private RawFactoryDataAndMetadata readSnapshot(Path snapshot) {
        try {
            return migrationManager.readRawFactoryDataAndMetadata(Files.readString(snapshot));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void checkMigrationManagerAvailable() {
        if (migrationManager == null) {
            throw new IllegalStateException("no migrationManager available, build the microservice with MicroserviceBuilder");
        }
    }

    private String exceptionSummary(Throwable e) {
        StringBuilder result = new StringBuilder(e.toString());
        Throwable cause = e.getCause();
        while (cause != null) {
            result.append(" caused by ").append(cause);
            cause = cause.getCause();
        }
        return result.toString();
    }
}

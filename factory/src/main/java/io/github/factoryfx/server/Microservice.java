package io.github.factoryfx.server;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.github.factoryfx.factory.FactoryUpdate;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.merge.AttributeDiffInfo;
import io.github.factoryfx.factory.merge.DataMerger;
import io.github.factoryfx.factory.merge.MergeDiffInfo;
import io.github.factoryfx.factory.storage.*;
import io.github.factoryfx.factory.storage.migration.MigrationManager;
import io.github.factoryfx.factory.validation.ValidationError;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.FactoryManager;
import io.github.factoryfx.factory.RootFactoryWrapper;
import io.github.factoryfx.factory.log.FactoryUpdateLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * starting point for factoryfx application
 *
 * @param <R> Root
 */
public class Microservice<L,R extends FactoryBase<L,R>> {
    private static final Logger logger = LoggerFactory.getLogger(Microservice.class);

    private final FactoryManager<L,R> factoryManager;
    private final DataStorage<R> dataStorage;
    private final FactoryTreeBuilder<L,R> factoryTreeBuilder;
    private final MigrationManager<R> migrationManager;

    public Microservice(FactoryManager<L,R> factoryManager, DataStorage<R> dataStorage, FactoryTreeBuilder<L,R> factoryTreeBuilder) {
        this(factoryManager, dataStorage, factoryTreeBuilder, null);
    }

    public Microservice(FactoryManager<L,R> factoryManager, DataStorage<R> dataStorage, FactoryTreeBuilder<L,R> factoryTreeBuilder, MigrationManager<R> migrationManager) {
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
    public synchronized void persistConfigurationPatches() {
        checkMigrationManagerAvailable();
        dataStorage.patchAll(migrationManager.createConfigurationPatcher());
    }

    /**
     * preflight check for the current configuration, see {@link #preflightCheck(boolean)}
     * @return report
     */
    public synchronized PreflightCheckReport preflightCheck() {
        return preflightCheck(false);
    }

    /**
     * check that this software version (factory classes, registered patches and migrations) can work with the
     * stored configuration, WITHOUT starting the application. intended for deployment tooling before switching to a
     * new software version.<br>
     * checks: the current configuration deserializes through the regular load path (patches, migrations, json binding),
     * the resulting factory tree has no validation errors and the treeBuilder rebuild succeeds.
     *
     * @param includeHistory additionally check that every history configuration can be loaded (slower)
     * @return report with all found problems, {@link PreflightCheckReport#isOk()} when safe to start
     */
    public synchronized PreflightCheckReport preflightCheck(boolean includeHistory) {
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
            }
            if (factoryTreeBuilder.isPersistentFactoryBuilder() && (currentRoot.internal().getTreeBuilderName() != null || currentRoot.internal().isTreeBuilderClassUsed())) {
                try {
                    factoryTreeBuilder.rebuildTreeForExistingConfiguration(currentRoot);
                } catch (RuntimeException e) {
                    problems.add("treeBuilder rebuild for the current configuration failed: " + exceptionSummary(e));
                }
            }
        }
        if (includeHistory) {
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

    /**
     * save a snapshot of the current configuration to a file, as stored (raw json, no patches or migrations applied).<br>
     * because the stored form is unchanged the snapshot can also be restored by an OLDER software version, making it a
     * rollback safety net before an upgrade: save a snapshot, then start the new version.
     *
     * @param target target file
     */
    public synchronized void saveConfigurationSnapshot(Path target) {
        checkMigrationManagerAvailable();
        RawFactoryDataAndMetadata raw = dataStorage.getCurrentDataRaw();
        try {
            Files.writeString(target, migrationManager.writeRawFactoryDataAndMetadata(raw));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * restore a configuration snapshot created with {@link #saveConfigurationSnapshot(Path)}: the snapshot is loaded
     * through the regular load path (patches and migrations apply) and stored as a NEW configuration version, the
     * existing history is preserved. if the microservice is started the running application is updated as well.<br>
     * the patched result is persisted, see {@link #loadConfigurationSnapshotRaw(Path)} to store the snapshot unchanged.
     *
     * @param snapshot snapshot file
     * @return update log when the microservice is started, null otherwise
     */
    public synchronized FactoryUpdateLog<R> loadConfigurationSnapshot(Path snapshot) {
        checkMigrationManagerAvailable();
        RawFactoryDataAndMetadata raw = readSnapshot(snapshot);
        R root = migrationManager.read(raw.root, raw.metadata);
        DataUpdate<R> update = new DataUpdate<>(root, "System", "restore configuration snapshot", dataStorage.getCurrentDataId());
        if (factoryManager.isStarted()) {
            return updateCurrentFactory(update);
        }
        dataStorage.updateCurrentData(update, null);
        return null;
    }

    /**
     * restore a configuration snapshot created with {@link #saveConfigurationSnapshot(Path)} WITHOUT baking the
     * registered patches and migrations into the stored form: the snapshot json and its configuration schema version
     * are stored unchanged as a NEW configuration version, the existing history is preserved. patches keep applying
     * on load as usual and the stored configuration remains readable by an older software version. if the microservice
     * is started the running application is updated as well (loaded through the regular load path, patches apply
     * in-memory).
     *
     * @param snapshot snapshot file
     * @return update log when the microservice is started, null otherwise
     */
    public synchronized FactoryUpdateLog<R> loadConfigurationSnapshotRaw(Path snapshot) {
        checkMigrationManagerAvailable();
        RawFactoryDataAndMetadata raw = readSnapshot(snapshot);

        FactoryUpdateLog<R> factoryLog = null;
        UpdateSummary changeSummary = null;
        if (factoryManager.isStarted()) {
            R root = migrationManager.read(raw.root, raw.metadata);
            //update the running application without persisting the patched tree, the snapshot is stored raw below
            factoryLog = factoryManager.update(factoryManager.getCurrentFactory().utility().copy(), root, (permission) -> true, true);
            if (factoryLog.failedUpdate() || !factoryLog.successfullyMerged()) {
                return factoryLog;
            }
            if (factoryLog.mergeDiffInfo != null) {
                changeSummary = createUpdateSummary(factoryLog.mergeDiffInfo);
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

    public MergeDiffInfo<R> getDiffToPreviousVersion(StoredDataMetadata storedDataMetadata) {
        R historyCurrent = getHistoryFactory(storedDataMetadata.mergerVersionId);
        R historyCommon = getHistoryFactory(storedDataMetadata.baseVersionId);
        R historyUpdate = getHistoryFactory(storedDataMetadata.id);
        return new DataMerger<>(historyCurrent,historyCommon,historyUpdate).createMergeResult((permission)->true).executeMerge();
    }

    public FactoryUpdateLog<R> revertTo(StoredDataMetadata storedDataMetadata, String user) {
        R historyFactory = getHistoryFactory(storedDataMetadata.id);
        DataAndId<R> currentFactory = dataStorage.getCurrentData();
        return updateCurrentFactory(new DataUpdate<>(
                historyFactory,
                user,
                "revert to configuration of timestamp: "+ DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(storedDataMetadata.creationTime) +
                        ", original comment: " + storedDataMetadata.comment,
                currentFactory.id)
        );
    }

    private UpdateSummary createUpdateSummary(MergeDiffInfo<R> mergeDiffInfo){
        return new UpdateSummary(mergeDiffInfo.mergeInfos);
    }

    public synchronized FactoryUpdateLog<R> updateCurrentFactory(DataUpdate<R> update) {
        //update based on the current configuration (the usual case): the common version is the currently running
        //factory tree, an in-memory copy avoids reading and deserializing it from the storage
        boolean baseVersionIsCurrent = factoryManager.isStarted() && update.baseVersionId.equals(dataStorage.getCurrentDataId());
        R commonVersion = baseVersionIsCurrent
                ? factoryManager.getCurrentFactory().utility().copy()
                : dataStorage.getHistoryData(update.baseVersionId);
        FactoryUpdateLog<R> factoryLog = factoryManager.update(commonVersion,update.root, update.permissionChecker, baseVersionIsCurrent);
        if (!factoryLog.failedUpdate() && factoryLog.successfullyMerged()){

            UpdateSummary changeSummary=null;
            if (factoryLog.mergeDiffInfo!=null){
                changeSummary=createUpdateSummary(factoryLog.mergeDiffInfo);
            }

            DataUpdate<R> updateAfterMerge = new DataUpdate<>(
                    factoryManager.getCurrentFactory(),
                    update.user,
                    update.comment,
                    update.baseVersionId
            );
            dataStorage.updateCurrentData(updateAfterMerge,changeSummary);
        }
        return factoryLog;
    }


    public synchronized MergeDiffInfo<R> simulateUpdateCurrentFactory(DataUpdate<R> possibleUpdate){
        boolean baseVersionIsCurrent = factoryManager.isStarted() && possibleUpdate.baseVersionId.equals(dataStorage.getCurrentDataId());
        R commonVersion = baseVersionIsCurrent
                ? factoryManager.getCurrentFactory().utility().copy()
                : dataStorage.getHistoryData(possibleUpdate.baseVersionId);
        return factoryManager.simulateUpdate(commonVersion , possibleUpdate.root, possibleUpdate.permissionChecker, baseVersionIsCurrent);
    }

    /**
     *  prepare a new factory which could be used to update data. mainly give it the correct baseVersionId
     *  @return new possible factory update with prepared ids/metadata
     * */
    public synchronized DataUpdate<R> prepareNewFactory() {
        if (!factoryManager.isStarted()){
           throw new IllegalStateException("Microservice is not started");
        }
        return prepareNewFactory("","");
    }

    /**
     *  Update from different process(browser java richclient)
     *  prepare a new factory which could be used to update data. mainly give it the correct baseVersionId
     * @param user use
     * @param comment comment
     * @return new possible factory update with prepared ids/metadata
     */
    public synchronized DataUpdate<R> prepareNewFactory(String user, String comment) {
        return new DataUpdate<>(
                factoryManager.getCurrentFactory().utility().copy(),
                user,
                comment,
                dataStorage.getCurrentDataId());
    }


    public R getHistoryFactory(String id) {
        return dataStorage.getHistoryData(id);
    }

    public Collection<StoredDataMetadata> getHistoryFactoryList(boolean light) {
        return dataStorage.getHistoryDataList(light);
    }

    public synchronized L start() {
        final DataAndId<R> currentFactory = dataStorage.getCurrentData();
        R currentFactoryRoot = currentFactory.root.internal().finalise();
        currentFactoryRoot.internal().setFactoryTreeBuilder(factoryTreeBuilder);

        if (factoryTreeBuilder.isPersistentFactoryBuilder()){
            //the current configuration is the reference: existing factories keep their values and wiring,
            //the FactoryTreeBuilder describes the technical configuration of the tree and only contributes newly introduced factories
            if (currentFactoryRoot.internal().getTreeBuilderName()!=null || currentFactoryRoot.internal().isTreeBuilderClassUsed()){
                R rebuildRoot = factoryTreeBuilder.rebuildTreeForExistingConfiguration(currentFactoryRoot);
                DataMerger<R> merge = new DataMerger<>(currentFactoryRoot,currentFactoryRoot.utility().copy(),rebuildRoot);
                MergeDiffInfo<R> mergeDiffInfo = merge.createMergeResult((p) -> true, true).executeMerge();

                if (mergeDiffInfo.successfullyMerged()){
                    if (!mergeDiffInfo.mergeInfos.isEmpty()){
                        DataUpdate<R> dataUpdate = new DataUpdate<>(currentFactoryRoot,"System","FactoryTreeBuilder update",currentFactory.id);
                        dataStorage.updateCurrentData(dataUpdate,new UpdateSummary(mergeDiffInfo.mergeInfos));
                    }
                } else {
                    logger.warn("can't apply changes from FactoryTreeBuilder to current storage Data");

                    Map<UUID, FactoryBase<?, R>> oldMap = currentFactoryRoot.internal().collectChildFactoryMap();
                    Map<UUID, FactoryBase<?, R>> newMap = currentFactoryRoot.internal().collectChildFactoryMap();
                    for (AttributeDiffInfo conflictInfo : mergeDiffInfo.conflictInfos) {
                        logger.warn("Conflict: "+ conflictInfo.getDiffDisplayText(oldMap,newMap));
                    }
                }
            }
        }


        currentFactoryRoot.internal().setMicroservice(this);//also mind ExceptionResponseAction#reset
        return factoryManager.start(new RootFactoryWrapper<>(currentFactoryRoot));
    }

    public synchronized void stop() {
        factoryManager.stop();
    }

    public L getRootLiveObject(){
        return factoryManager.getCurrentFactory().internal().getLiveObject();
    }

    /**
     * updates the current factories from the same process(jvm)
     * @param updater update execution
     */
    public void update(FactoryUpdate<R> updater){
        factoryManager.update(updater);
        dataStorage.updateCurrentData(new DataUpdate<>(factoryManager.getCurrentFactory(),"system","",dataStorage.getCurrentDataId()),null);
    }

}

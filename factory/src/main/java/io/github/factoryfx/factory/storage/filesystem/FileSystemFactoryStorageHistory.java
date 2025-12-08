package io.github.factoryfx.factory.storage.filesystem;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.jackson.OutputStyle;
import io.github.factoryfx.factory.jackson.SimpleObjectMapper;
import io.github.factoryfx.factory.storage.DataStoragePatcher;
import io.github.factoryfx.factory.storage.StoredDataMetadata;
import io.github.factoryfx.factory.storage.migration.MigrationManager;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class FileSystemFactoryStorageHistory<R extends FactoryBase<?, R>> {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(FileSystemFactoryStorageHistory.class);

    private final Map<String, StoredDataMetadata> cache = new TreeMap<>();
    private final Path historyDirectory;
    private final MigrationManager<R> migrationManager;
    private final int maxConfigurationHistory;
    private final SimpleObjectMapper objectMapper;


    public FileSystemFactoryStorageHistory(Path basePath, MigrationManager<R> migrationManager, SimpleObjectMapper objectMapper) {
        this(basePath, migrationManager, objectMapper, Integer.MAX_VALUE);
    }

    public FileSystemFactoryStorageHistory(Path basePath, MigrationManager<R> migrationManager, SimpleObjectMapper objectMapper, int maxConfigurationHistory) {
        this.migrationManager = migrationManager;
        this.historyDirectory = basePath.resolve("history");
        this.maxConfigurationHistory = maxConfigurationHistory;
        this.objectMapper = objectMapper;
        if (!Files.exists(historyDirectory)) {
            try {
                Files.createDirectories(historyDirectory);
            } catch (IOException e) {
                throw new IllegalStateException("Unable to create path" + historyDirectory.toFile().getAbsolutePath(), e);
            }
        }
    }

    public R getHistoryFactory(String id) {
        StoredDataMetadata storedDataMetadata = null;
        for (StoredDataMetadata metaData : getHistoryFactoryList()) {
            if (metaData.id.equals(id)) {
                storedDataMetadata = metaData;

            }
        }
        if (storedDataMetadata == null) {
            throw new IllegalStateException("cant find storedDataMetadata for factory: " + id + " in history");
        }
        return migrationManager.read(readFile(Paths.get(historyDirectory.toString() + "/" + id + ".json")), storedDataMetadata.dataStorageMetadataDictionary);
    }

    private void visitHistoryFiles(Consumer<Path> visitor) {
        try (Stream<Path> files = Files.walk(historyDirectory).filter(Files::isRegularFile)) {
            files.forEach(visitor);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Collection<StoredDataMetadata> getHistoryFactoryList() {
        if (cache.isEmpty()) {
            visitHistoryFiles(path -> {
                if (path.toString().endsWith("_metadata.json")) {
                    StoredDataMetadata storedDataMetadata = migrationManager.readStoredFactoryMetadata(readFile(path));
                    cache.put(storedDataMetadata.id, storedDataMetadata);
                }
            });
        }
        return cache.values();
    }

    public void updateHistory(R factoryRoot, StoredDataMetadata metadata) {
        String id = metadata.id;

        writeFile(historyDirectory.resolve(id + ".json"), objectMapper.writeValueAsString(factoryRoot, OutputStyle.COMPACT));
        writeFile(historyDirectory.resolve(id + "_metadata.json"), objectMapper.writeValueAsString(metadata, OutputStyle.COMPACT));
        cache.put(id, metadata);

        houseKeeping();

    }

    private String readFile(Path path) {
        try {
            return Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeFile(Path path, String content) {
        try {
            Files.writeString(path, content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void patchAll(DataStoragePatcher consumer) {
        visitHistoryFiles(path -> {
            if (!path.toString().endsWith("_metadata.json")) {
                JsonNode data = objectMapper.readTree(path);
                Path metadataPath = path.resolveSibling(path.getParent().resolve(path.getFileName().toString().replace(".json", "_metadata.json")));
                JsonNode metadata = objectMapper.readTree(metadataPath);
                consumer.patch((ObjectNode) data, metadata, objectMapper);
                writeFile(path, objectMapper.writeValueAsString(data, OutputStyle.COMPACT));
                writeFile(metadataPath, objectMapper.writeValueAsString(metadata, OutputStyle.COMPACT));
            }
        });
    }

    public void patchForId(DataStoragePatcher consumer, String id) {
        visitHistoryFiles(path -> {
            if (!path.toString().endsWith("_metadata.json") && (id + ".json").equals(path.getFileName().toString())) {
                JsonNode data = objectMapper.readTree(path);
                Path metadataPath = path.resolveSibling(path.getParent().resolve(path.getFileName().toString().replace(".json", "_metadata.json")));
                JsonNode metadata = objectMapper.readTree(metadataPath);
                consumer.patch((ObjectNode) data, metadata, objectMapper);
                writeFile(path, objectMapper.writeValueAsString(data, OutputStyle.COMPACT));
                writeFile(metadataPath, objectMapper.writeValueAsString(metadata, OutputStyle.COMPACT));
            }
        });
    }

    private void houseKeeping() {
        if (maxConfigurationHistory == Integer.MAX_VALUE) {return;}
        List<StoredDataMetadata> collect = getHistoryFactoryList().stream().toList();
        int numToRemove = collect.size() - maxConfigurationHistory;
        if (numToRemove > 0) {
            collect.stream().sorted(Comparator.comparing(a -> a.creationTime)).limit(numToRemove).forEach(smd -> {
                if (!smd.isInitialFactory()) {
                    try {
                        Files.deleteIfExists(historyDirectory.resolve(smd.id + ".json"));
                        Files.deleteIfExists(historyDirectory.resolve(smd.id + "_metadata.json"));
                        cache.remove(smd.id);
                    } catch (IOException e) {
                        logger.warn("Could not remove configuration files", e);
                    }
                }
            });
        }
    }
}

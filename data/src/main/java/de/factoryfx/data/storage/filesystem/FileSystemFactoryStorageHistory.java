package de.factoryfx.data.storage.filesystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

import de.factoryfx.data.Data;
import de.factoryfx.data.storage.migration.MigrationManager;
import de.factoryfx.data.storage.StoredDataMetadata;

public class FileSystemFactoryStorageHistory<R extends Data,S> {
    private final Map<String,StoredDataMetadata<S>> cache = new TreeMap<>();
    private final Path historyDirectory;
    private final MigrationManager<R,S> migrationManager;

    public FileSystemFactoryStorageHistory(Path basePath, MigrationManager<R,S> migrationManager){
        this.migrationManager = migrationManager;
        historyDirectory= Paths.get(basePath.toString()+"/history/");
        if (!Files.exists(historyDirectory)){
            if (!historyDirectory.toFile().mkdirs()){
                throw new IllegalStateException("Unable to create path"+historyDirectory.toFile().getAbsolutePath());
            }
        }
    }

    public R getHistoryFactory(String id) {
        StoredDataMetadata<S> storedDataMetadata=null;
        for(StoredDataMetadata<S> metaData: getHistoryFactoryList()){
            if (metaData.id.equals(id)){
                storedDataMetadata=metaData;

            }
        }
        if (storedDataMetadata==null) {
            throw new IllegalStateException("cant find storedDataMetadata for factory: "+id+" in history");
        }
        return migrationManager.read(readFile(Paths.get(historyDirectory.toString()+"/"+id+".json")),storedDataMetadata);
    }

    public Collection<StoredDataMetadata<S>> getHistoryFactoryList() {
        if (cache.isEmpty()) {
            try (Stream<Path> files = Files.walk(historyDirectory).filter(Files::isRegularFile)){
                files.forEach(path -> {
                    if (path.toString().endsWith("_metadata.json")){
                        StoredDataMetadata<S> storedDataMetadata = migrationManager.readStoredFactoryMetadata(readFile(path));
                        cache.put(storedDataMetadata.id, storedDataMetadata);
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return cache.values();
    }

    public void updateHistory(R factoryRoot, StoredDataMetadata<S> metadata) {
        String id=metadata.id;

        writeFile(Paths.get(historyDirectory.toString()+"/"+id+".json"), migrationManager.write(factoryRoot));
        writeFile(Paths.get(historyDirectory.toString()+"/"+id+"_metadata.json"), migrationManager.writeStorageMetadata(metadata));
        cache.put(id,metadata);

    }

    private String readFile(Path path){
        try {
            return Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeFile(Path path, String content){
        try {
            Files.writeString(path,content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

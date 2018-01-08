package de.factoryfx.data.storage.filesystem;

import static java.nio.file.Files.readAllBytes;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

import de.factoryfx.data.Data;
import de.factoryfx.data.storage.DataSerialisationManager;
import de.factoryfx.data.storage.StoredDataMetadata;

public class FileSystemFactoryStorageHistory<R extends Data,S> {
    private final Map<String,StoredDataMetadata<S>> cache = new TreeMap<>();
    private final Path historyDirectory;
    private final DataSerialisationManager<R,S> dataSerialisationManager;

    public FileSystemFactoryStorageHistory(Path basePath, DataSerialisationManager<R,S> dataSerialisationManager){
        this.dataSerialisationManager = dataSerialisationManager;
        historyDirectory= Paths.get(basePath.toString()+"/history/");
        if (!Files.exists(historyDirectory)){
            if (!historyDirectory.toFile().mkdirs()){
                throw new IllegalStateException("Unable to create path"+historyDirectory.toFile().getAbsolutePath());
            }
        }
    }

    public R getHistoryFactory(String id) {
        int dataModelVersion=-99999;
        for(StoredDataMetadata metaData: getHistoryFactoryList()){
            if (metaData.id.equals(id)){
                dataModelVersion=metaData.dataModelVersion;

            }
        }
        if (dataModelVersion==-99999) {
            throw new IllegalStateException("cant find id: "+id+" in history");
        }


        return dataSerialisationManager.read(readFile(Paths.get(historyDirectory.toString()+id+".json")),dataModelVersion);
    }

    public Collection<StoredDataMetadata<S>> getHistoryFactoryList() {
        return cache.values();
    }

    public void initFromFileSystem(){
        try {
            try (Stream<Path> files = Files.walk(historyDirectory).filter(Files::isRegularFile)){
                files.forEach(path -> {
                    if (path.toString().endsWith("_metadata.json")){
                        StoredDataMetadata<S> storedDataMetadata = dataSerialisationManager.readStoredFactoryMetadata(readFile(path));
                        cache.put(storedDataMetadata.id, storedDataMetadata);
                    }
                });
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateHistory(StoredDataMetadata<S> metadata, R factoryRoot) {
        String id=metadata.id;

        writeFile(Paths.get(historyDirectory.toString()+"/"+id+".json"), dataSerialisationManager.write(factoryRoot));
        writeFile(Paths.get(historyDirectory.toString()+"/"+id+"_metadata.json"), dataSerialisationManager.writeStorageMetadata(metadata));
        cache.put(id,metadata);

    }

    private String readFile(Path path){
        try {
            return new String(readAllBytes(path), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeFile(Path path, String content){
        try {
            Files.write(path,content.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

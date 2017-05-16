package de.factoryfx.factory.datastorage.filesystem;

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

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.datastorage.FactorySerialisationManager;
import de.factoryfx.factory.datastorage.StoredFactoryMetadata;

public class FileSystemFactoryStorageHistory<V,L,R extends FactoryBase<L,V>> {
    private Map<String,StoredFactoryMetadata> cache = new TreeMap<>();
    private Path historyDirectory;
    private final FactorySerialisationManager<R> factorySerialisationManager;

    public FileSystemFactoryStorageHistory(Path basePath, FactorySerialisationManager<R> factorySerialisationManager){
        this.factorySerialisationManager= factorySerialisationManager;
        historyDirectory= Paths.get(basePath.toString()+"/history/");
        if (!Files.exists(historyDirectory)){
            if (!historyDirectory.toFile().mkdirs()){
                throw new IllegalStateException("Unable to create path"+historyDirectory.toFile().getAbsolutePath());
            }
        }
    }

    public R getHistoryFactory(String id) {
        int dataModelVersion=-99999;
        for(StoredFactoryMetadata metaData: getHistoryFactoryList()){
            if (metaData.id.equals(id)){
                dataModelVersion=metaData.dataModelVersion;

            }
        }
        if (dataModelVersion==-99999) {
            throw new IllegalStateException("cant find id: "+id+" in history");
        }


        return factorySerialisationManager.read(readFile(Paths.get(historyDirectory.toString()+id+".json")),dataModelVersion);
    }

    public Collection<StoredFactoryMetadata> getHistoryFactoryList() {
        return cache.values();
    }

    public void initFromFileSystem(){
        try {
            try (Stream<Path> files = Files.walk(historyDirectory).filter(Files::isRegularFile)){
                files.forEach(path -> {
                    if (path.toString().endsWith("_metadata.json")){
                        StoredFactoryMetadata storedFactoryMetadata=factorySerialisationManager.readStoredFactoryMetadata(readFile(path));
                        cache.put(storedFactoryMetadata.id,storedFactoryMetadata);
                    }
                });
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateHistory(StoredFactoryMetadata metadata, R factoryRoot) {
        String id=metadata.id;

        writeFile(Paths.get(historyDirectory.toString()+"/"+id+".json"),factorySerialisationManager.write(factoryRoot));
        writeFile(Paths.get(historyDirectory.toString()+"/"+id+"_metadata.json"),factorySerialisationManager.writeStorageMetadata(metadata));
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

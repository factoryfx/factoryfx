package de.factoryfx.factory.datastorage.filesystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.datastorage.StoredFactoryMetadata;
import de.factoryfx.data.jackson.ObjectMapperBuilder;

public class FileSystemFactoryStorageHistory<L,V,T extends FactoryBase<L,V>> {
    private Map<String,StoredFactoryMetadata> cache = new TreeMap<>();
    private Path historyDirectory;
    private final Class<T> rootClass;

    public FileSystemFactoryStorageHistory(Path basePath, Class<T> rootClass){
        historyDirectory= Paths.get(basePath.toString()+"/history/");
        if (!Files.exists(historyDirectory)){
            if (!historyDirectory.toFile().mkdirs()){
                throw new IllegalStateException("Unable to create path"+historyDirectory.toFile().getAbsolutePath());
            }
        }
        this.rootClass =rootClass;
    }

    public T getHistoryFactory(String id) {
        return ObjectMapperBuilder.build().readValue(Paths.get(historyDirectory.toString()+id+".json").toFile(),rootClass);
    }

    public Collection<StoredFactoryMetadata> getHistoryFactoryList() {
        return cache.values();
    }

    public void initFromFileSystem(){
        try {
            try (Stream<Path> files = Files.walk(historyDirectory).filter(Files::isRegularFile)){
                files.forEach(path -> {
                    if (path.toString().endsWith("_metadata.json")){
                        StoredFactoryMetadata storedFactoryMetadata=ObjectMapperBuilder.build().readValue(path.toFile(),StoredFactoryMetadata.class);
                        cache.put(storedFactoryMetadata.id,storedFactoryMetadata);
                    }
                });
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateHistory(StoredFactoryMetadata metadata, T factoryRoot) {
        String id=metadata.id;
        ObjectMapperBuilder.build().writeValue(Paths.get(historyDirectory.toString()+"/"+id+".json").toFile(),factoryRoot);
        ObjectMapperBuilder.build().writeValue(Paths.get(historyDirectory.toString()+"/"+id+"_metadata.json").toFile(),metadata);
        cache.put(id,metadata);

    }
}

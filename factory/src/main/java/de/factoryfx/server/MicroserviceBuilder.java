package de.factoryfx.server;

import de.factoryfx.data.merge.DataMerger;
import de.factoryfx.data.merge.MergeDiffInfo;
import de.factoryfx.data.storage.*;
import de.factoryfx.data.storage.filesystem.FileSystemDataStorage;
import de.factoryfx.data.storage.inmemory.InMemoryDataStorage;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.FactoryManager;
import de.factoryfx.factory.exception.AllOrNothingFactoryExceptionHandler;
import de.factoryfx.factory.exception.LoggingFactoryExceptionHandler;
import de.factoryfx.factory.exception.RethrowingFactoryExceptionHandler;
import de.factoryfx.factory.log.FactoryUpdateLog;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;


public class MicroserviceBuilder {

    /**
     * Microservice without a persistence data storage
     *
     * @param root factory root
     * @return microservice*/
    public static <V,R extends FactoryBase<?,V,R>,S> Microservice<V,R,S> buildInMemoryMicroservice(R root){
        return new Microservice<>(new FactoryManager<>(new RethrowingFactoryExceptionHandler()), new InMemoryDataStorage<>(root));
    }


    /**
     * Microservice with filesystem data storage
     * @param root factory root
     * @param path filesystem path to store
     * @param <V> Visitor
     * @param <R> Root
     * @param <S> Summary
     * @return microservice
     */
    @SuppressWarnings("unchecked")
    public static <V,R extends FactoryBase<?,V,R>,S> Microservice<V,R,S> buildFilesystemMicroservice(R root, Path path){
        Class<R> rootClass = (Class<R>) root.getClass();
        DataSerialisationManager<R,S> defaultSerialisationManager = new DataSerialisationManager<>(new JacksonSerialisation<>(1),new JacksonDeSerialisation<>(rootClass,1),new ArrayList<>(),1);
        return new Microservice<>(new FactoryManager<>(new LoggingFactoryExceptionHandler(new AllOrNothingFactoryExceptionHandler())), new FileSystemDataStorage<>(path, root,defaultSerialisationManager));
    }


}

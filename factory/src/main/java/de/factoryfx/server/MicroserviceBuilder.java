package de.factoryfx.server;

import de.factoryfx.data.storage.*;
import de.factoryfx.data.storage.filesystem.FileSystemDataStorage;
import de.factoryfx.data.storage.inmemory.InMemoryDataStorage;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.FactoryManager;
import de.factoryfx.factory.exception.AllOrNothingFactoryExceptionHandler;
import de.factoryfx.factory.exception.LoggingFactoryExceptionHandler;
import de.factoryfx.factory.exception.ResettingFactoryExceptionHandler;
import de.factoryfx.factory.exception.RethrowingFactoryExceptionHandler;

import java.nio.file.Path;
import java.util.ArrayList;


public class MicroserviceBuilder {

    /**
     * Microservice without a persistence data storage
     *
     * @param root factory root
     * @param <V> Visitor
     * @param <R> Root
     * @param <S> Summary
     * @return microservice
     */
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
        return new Microservice<>(new FactoryManager<>(new LoggingFactoryExceptionHandler(new ResettingFactoryExceptionHandler())), new FileSystemDataStorage<>(path, root,defaultSerialisationManager));
    }


}

package de.factoryfx.server;

import de.factoryfx.data.storage.filesystem.FileSystemDataStorage;
import de.factoryfx.data.storage.inmemory.InMemoryDataStorage;
import de.factoryfx.data.storage.migration.MigrationManager;
import de.factoryfx.data.storage.migration.GeneralStorageMetadataBuilder;
import de.factoryfx.data.storage.migration.metadata.DataStorageMetadataDictionary;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.FactoryManager;
import de.factoryfx.factory.builder.FactoryTreeBuilder;
import de.factoryfx.factory.exception.LoggingFactoryExceptionHandler;
import de.factoryfx.factory.exception.ResettingHandler;
import de.factoryfx.factory.exception.RethrowingFactoryExceptionHandler;

import java.nio.file.Path;
import java.util.ArrayList;


public class MicroserviceBuilder {

    /**
     * Microservice without a persistence data storage
     *
     * @param rootFactory factory root
     * @param <V> Visitor
     * @param <L> root liveobject
     * @param <R> Root
     * @param <S> Summary
     * @return microservice
     */
    public static <V,L,R extends FactoryBase<L,V,R>,S> Microservice<V,L,R,S> buildInMemoryMicroservice(R rootFactory){
        return new Microservice<>(new FactoryManager<>(new RethrowingFactoryExceptionHandler()), new InMemoryDataStorage<>(rootFactory));
    }

    /**
     * Microservice without a persistence data storage
     *
     * @param factoryTreeBuilder factoryTreeBuilder
     * @param <V> Visitor
     * @param <L> root liveobject
     * @param <R> Root
     * @param <S> Summary
     * @return microservice
     */
    public static <V,L,R extends FactoryBase<L,V,R>,S> Microservice<V,L,R,S> buildInMemoryMicroservice(FactoryTreeBuilder<R> factoryTreeBuilder){
        return new Microservice<>(new FactoryManager<>(new RethrowingFactoryExceptionHandler()), new InMemoryDataStorage<>(factoryTreeBuilder.buildTree()));
    }



    /**
     * Microservice with filesystem data storage
     * @param rootFactory factory root
     * @param path filesystem path to store
     * @param <V> Visitor
     * @param <L> root liveobject
     * @param <R> Root
     * @param <S> Summary
     * @return microservice
     */
    @SuppressWarnings("unchecked")
    public static <V,L,R extends FactoryBase<L,V,R>,S> Microservice<V,L,R,S> buildFilesystemMicroservice(R rootFactory, Path path){
        Class<R> rootClass = (Class<R>) rootFactory.getClass();
        MigrationManager<R,S> migrationManager = new MigrationManager<>(rootClass, new ArrayList<>(), GeneralStorageMetadataBuilder.build(), new ArrayList<>(), new DataStorageMetadataDictionary(rootFactory.getClass()));
        return new Microservice<>(new FactoryManager<>(new LoggingFactoryExceptionHandler(new ResettingHandler())), new FileSystemDataStorage<>(path, rootFactory, migrationManager));
    }


}

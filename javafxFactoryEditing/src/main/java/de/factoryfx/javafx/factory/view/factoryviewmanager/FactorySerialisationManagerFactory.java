package de.factoryfx.javafx.factory.view.factoryviewmanager;

import de.factoryfx.data.Data;
import de.factoryfx.data.storage.DataSerialisationManager;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;

/**
 *
 * @param <RS> root server
 *
 * @param <S>  server historysummary
 * @param <V>  visitor
 * @param <R>  root
 */
public abstract class FactorySerialisationManagerFactory<RS extends Data,S, V, R extends FactoryBase<?,V,R>> extends SimpleFactoryBase<DataSerialisationManager<RS,S>,V,R> {

}

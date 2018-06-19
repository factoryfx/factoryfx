package de.factoryfx.javafx.factory.view.factoryviewmanager;

import de.factoryfx.data.Data;
import de.factoryfx.data.storage.DataSerialisationManager;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.javafx.factory.RichClientRoot;

/**
 *
 * @param <RS> root server
 * @param <S>  server historysummary
 */
public abstract class FactorySerialisationManagerFactory<RS extends Data,S> extends SimpleFactoryBase<DataSerialisationManager<RS,S>,Void,RichClientRoot> {

}
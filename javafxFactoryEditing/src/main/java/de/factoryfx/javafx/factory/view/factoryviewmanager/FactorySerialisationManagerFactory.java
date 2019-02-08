package de.factoryfx.javafx.factory.view.factoryviewmanager;

import de.factoryfx.data.Data;
import de.factoryfx.data.storage.migration.MigrationManager;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.javafx.factory.RichClientRoot;

/**
 *
 * @param <RS> root server
 * @param <S>  server history summary
 */
public abstract class FactorySerialisationManagerFactory<RS extends Data,S> extends SimpleFactoryBase<MigrationManager<RS,S>,Void,RichClientRoot> {

}

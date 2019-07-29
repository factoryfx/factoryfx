package io.github.factoryfx.javafx.factoryviewmanager;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.storage.migration.MigrationManager;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.javafx.RichClientRoot;

/**
 *
 * @param <RS> root server
 */
public abstract class FactorySerialisationManagerFactory<RS extends FactoryBase<?,RS>> extends SimpleFactoryBase<MigrationManager<RS>,RichClientRoot> {

}

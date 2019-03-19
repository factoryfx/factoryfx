package io.github.factoryfx.javafx.factory.view.factoryviewmanager;

import io.github.factoryfx.data.Data;
import io.github.factoryfx.data.storage.migration.MigrationManager;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.javafx.factory.RichClientRoot;

/**
 *
 * @param <RS> root server
 * @param <S>  server history summary
 */
public abstract class FactorySerialisationManagerFactory<RS extends Data,S> extends SimpleFactoryBase<MigrationManager<RS,S>,RichClientRoot> {

}

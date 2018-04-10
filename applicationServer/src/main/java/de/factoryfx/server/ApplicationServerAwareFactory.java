package de.factoryfx.server;

import de.factoryfx.data.attribute.types.ObjectValueAttribute;
import de.factoryfx.factory.FactoryBase;

/**
 * access application server from factory<br>
 * e.g to trigger self update<br>
 * example in ApplicationServerResourceFactory<br>
 *
 * @param <V> visitor
 * @param <RL> root live object
 * @param <R> root
 * @param <FL> factory live object
 * @param <S> Storage Summary
 */
public abstract class ApplicationServerAwareFactory<V,RL, R extends FactoryBase<RL,V>, FL,S> extends FactoryBase<FL,V> {

    public final ObjectValueAttribute<ApplicationServer<V, RL, R,S>> applicationServer = new ObjectValueAttribute<ApplicationServer<V, RL, R,S>>().labelText("application server").nullable();

}

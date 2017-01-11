package de.factoryfx.server;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.types.ObjectValueAttribute;
import de.factoryfx.factory.FactoryBase;

/**
 *
 * @param <V> visitor
 * @param <RL> root live object
 * @param <R> root
 * @param <FL> factory live object
 */
public abstract class ApplicationServerAwareFactory<V,RL, R extends FactoryBase<RL,V>, FL> extends FactoryBase<FL,V> {

    public final ObjectValueAttribute<ApplicationServer<RL,V, R>> applicationServer = new ObjectValueAttribute<>(new AttributeMetadata().labelText("application server"));

}

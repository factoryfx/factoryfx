package io.github.factoryfx.docu.initializr;

import io.github.factoryfx.factory.FactoryBase;

/**
 * adds ServerRootFactory als generic type */
public class FactoryListAttribute<L, F extends FactoryBase<L, ServerRootFactory>> extends io.github.factoryfx.factory.attribute.dependency.FactoryListAttribute<L, F> {
}

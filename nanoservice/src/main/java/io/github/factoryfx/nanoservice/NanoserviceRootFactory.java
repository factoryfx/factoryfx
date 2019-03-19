package io.github.factoryfx.nanoservice;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.SimpleFactoryBase;

/**
 *
 * @param <R> nano service result
 * @param <L> Live object nano service r
 * @param <NR> nano service root
 */
public abstract  class NanoserviceRootFactory<R,L extends NanoserviceRoot<R>, NR extends FactoryBase<?, NR>> extends SimpleFactoryBase<L,NR> {

}

package de.factoryfx.server.nanoservice;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;

/**
 *
 * @param <R> nano service result
 * @param <L> Live object nano service r
 * @param <NR> nano service root
 */
public abstract  class NanoserviceRootFactory<R,L extends NanoserviceRoot<R>, NR extends FactoryBase<?,Void, NR>> extends SimpleFactoryBase<L,Void,NR> {

}

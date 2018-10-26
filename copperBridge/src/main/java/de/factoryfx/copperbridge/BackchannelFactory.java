package de.factoryfx.copperbridge;

import org.copperengine.core.util.Backchannel;
import org.copperengine.core.util.BackchannelDefaultImpl;

import de.factoryfx.factory.FactoryBase;

public class BackchannelFactory<V, R extends FactoryBase<?, V, R>> extends FactoryBase<Backchannel, V, R> {

    //must be singleton
    public BackchannelFactory() {
        configLifeCycle().setCreator(BackchannelDefaultImpl::new);
    }
}

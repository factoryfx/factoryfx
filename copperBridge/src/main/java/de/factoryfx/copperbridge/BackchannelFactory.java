package de.factoryfx.copperbridge;

import org.copperengine.core.util.Backchannel;
import org.copperengine.core.util.BackchannelDefaultImpl;

import de.factoryfx.factory.FactoryBase;

public class BackchannelFactory<R extends FactoryBase<?, R>> extends FactoryBase<Backchannel, R> {

    //must be singleton
    public BackchannelFactory() {
        configLifeCycle().setCreator(BackchannelDefaultImpl::new);
    }
}

package io.github.factoryfx.copperbridge;

import io.github.factoryfx.factory.FactoryBase;
import org.copperengine.core.util.Backchannel;
import org.copperengine.core.util.BackchannelDefaultImpl;

public class BackchannelFactory<R extends FactoryBase<?, R>> extends FactoryBase<Backchannel, R> {

    //must be singleton
    public BackchannelFactory() {
        configLifeCycle().setCreator(BackchannelDefaultImpl::new);
    }
}

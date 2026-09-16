package io.github.factoryfx.docu.systemtree;

import java.util.Map;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.jetty.builder.JettyServerRootFactory;

public class ContractResourceFactory extends SimpleFactoryBase<ContractResource, JettyServerRootFactory> {

    @Override
    protected ContractResource createImpl() {
        return new ContractResource(Map.of(GreetingApi.class.getName(), ApiContractHash.hashOf(GreetingApi.class)));
    }
}

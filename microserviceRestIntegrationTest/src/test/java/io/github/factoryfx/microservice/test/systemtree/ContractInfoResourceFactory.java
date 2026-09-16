package io.github.factoryfx.microservice.test.systemtree;

import java.util.Map;

import io.github.factoryfx.factory.SimpleFactoryBase;

public class ContractInfoResourceFactory extends SimpleFactoryBase<ContractInfoResource, ARoot> {

    @Override
    protected ContractInfoResource createImpl() {
        return new ContractInfoResource(Map.of(TimeApi.class.getName(), ApiContractHash.hashOf(TimeApi.class)));
    }
}

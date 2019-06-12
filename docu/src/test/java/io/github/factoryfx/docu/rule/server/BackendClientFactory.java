package io.github.factoryfx.docu.rule.server;

import io.github.factoryfx.factory.attribute.primitive.IntegerAttribute;

public class BackendClientFactory extends ServerBaseFactory<BackendClient> {

    public final IntegerAttribute backendPort = new IntegerAttribute().labelText("Backend Port");

    public BackendClientFactory(){
        this.config().setDisplayTextProvider(()->"Backend Client");
    }

    @Override
    protected BackendClient createImpl() {
        return new BackendClient(backendPort.get());
    }
}

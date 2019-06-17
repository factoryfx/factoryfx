package io.github.factoryfx.docu.rule.simulator;


public class HelloResourceFactory extends SimulatorBaseFactory<HelloResource> {

    @Override
    protected HelloResource createImpl() {
        return new HelloResource();
    }

    public HelloResourceFactory(){
        this.config().setDisplayTextProvider(()->"Hello Resource");
    }
}

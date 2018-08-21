package de.factoryfx.soap;


import de.factoryfx.soap.example.HelloWorld;
import de.factoryfx.soap.server.SoapJettyServerFactory;
import de.factoryfx.factory.SimpleFactoryBase;

public class HelloWorldImplFactory extends SimpleFactoryBase<HelloWorld, Void, SoapJettyServerFactory> {

    @Override
    public HelloWorldImpl createImpl() {
        return new HelloWorldImpl();
    }
}

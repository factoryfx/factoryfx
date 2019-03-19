package io.github.factoryfx.soap;


import io.github.factoryfx.data.attribute.types.ObjectValueAttribute;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.soap.example.HelloWorld;
import io.github.factoryfx.soap.server.SoapJettyServerFactory;

public class HelloWorldFactory extends SimpleFactoryBase<HelloWorld, SoapJettyServerFactory> {

    public final ObjectValueAttribute<HelloWorld> service = new ObjectValueAttribute<>();

    public HelloWorld createImpl() {
        return service.get();
    }
}

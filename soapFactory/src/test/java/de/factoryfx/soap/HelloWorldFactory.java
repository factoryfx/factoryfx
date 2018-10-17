package de.factoryfx.soap;


import de.factoryfx.data.attribute.types.ObjectValueAttribute;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.soap.example.HelloWorld;
import de.factoryfx.soap.server.SoapJettyServerFactory;

public class HelloWorldFactory extends SimpleFactoryBase<HelloWorld, Void, SoapJettyServerFactory> {

    public final ObjectValueAttribute<HelloWorld> service = new ObjectValueAttribute<>();

    public HelloWorld createImpl() {
        return service.get();
    }
}

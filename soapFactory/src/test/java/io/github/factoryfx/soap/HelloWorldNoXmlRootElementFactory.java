package io.github.factoryfx.soap;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.types.ObjectValueAttribute;
import io.github.factoryfx.soap.examplenoroot.HelloWorldNoXmlRootElement;
import io.github.factoryfx.soap.server.SoapJettyServerFactory;

public class HelloWorldNoXmlRootElementFactory extends SimpleFactoryBase<HelloWorldNoXmlRootElement, SoapJettyServerFactory> {
    public final ObjectValueAttribute<HelloWorldNoXmlRootElement> service = new ObjectValueAttribute<>();


    @Override
    protected HelloWorldNoXmlRootElement createImpl() {
        return service.get();
    }
}

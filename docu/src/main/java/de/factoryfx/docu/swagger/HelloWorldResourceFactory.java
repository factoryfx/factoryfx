package de.factoryfx.docu.swagger;

import de.factoryfx.factory.SimpleFactoryBase;

public class HelloWorldResourceFactory extends SimpleFactoryBase<HelloWorldResource, Main.SwaggerWebserver> {

    @Override
    public HelloWorldResource createImpl() {
        return new HelloWorldResource();
    }

}

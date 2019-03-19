package io.github.factoryfx.docu.swagger;

import io.github.factoryfx.factory.SimpleFactoryBase;

public class HelloWorldResourceFactory extends SimpleFactoryBase<HelloWorldResource, Main.SwaggerWebserver> {

    @Override
    public HelloWorldResource createImpl() {
        return new HelloWorldResource();
    }

}

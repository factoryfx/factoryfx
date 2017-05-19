package de.factoryfx.docu.helloworld;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.SimpleFactoryBase;

public class HelloWorldFactory extends SimpleFactoryBase<HelloWorld,Void>{
    public final StringAttribute text=new StringAttribute(new AttributeMetadata().labelText("Text"));

    @Override
    public HelloWorld createImpl() {
        return new HelloWorld(text.get());
    }
}

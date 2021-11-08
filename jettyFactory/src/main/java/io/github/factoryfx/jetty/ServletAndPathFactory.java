package io.github.factoryfx.jetty;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryPolymorphicAttribute;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import jakarta.servlet.Servlet;

public class ServletAndPathFactory<R extends FactoryBase<?,R>> extends SimpleFactoryBase<ServletAndPath,R> {

    public final StringAttribute pathSpec = new StringAttribute().labelText("pathSpec");
    public final FactoryPolymorphicAttribute<Servlet> servlet = new FactoryPolymorphicAttribute<Servlet>().labelText("servlets");

    @Override
    protected ServletAndPath createImpl() {
        return new ServletAndPath(pathSpec.get(),servlet.instance());
    }

    public ServletAndPathFactory(){
        this.config().setDisplayTextProvider(pathSpec::get);
    }

}

package io.github.factoryfx.jetty;

import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryPolymorphicReferenceAttribute;

import javax.servlet.Servlet;

public class ServletAndPathFactory<R extends FactoryBase<?,R>> extends SimpleFactoryBase<ServletAndPath,R> {

    public final StringAttribute pathSpec = new StringAttribute().labelText("pathSpec");
    public final FactoryPolymorphicReferenceAttribute<R,Servlet> servlet = new FactoryPolymorphicReferenceAttribute<R,Servlet>().labelText("Servlet");


    @Override
    public ServletAndPath createImpl() {
        return new ServletAndPath(pathSpec.get(),servlet.instance());
    }
}

package io.github.factoryfx.jetty;

import io.github.factoryfx.data.attribute.types.StringAttribute;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.atrribute.FactoryPolymorphicReferenceAttribute;

import javax.servlet.Servlet;

public class ServletAndPathFactory<R extends FactoryBase<?,R>> extends SimpleFactoryBase<ServletAndPath,R> {

    public final StringAttribute pathSpec = new StringAttribute().labelText("pathSpec");
    public final FactoryPolymorphicReferenceAttribute<Servlet> servlet = new FactoryPolymorphicReferenceAttribute<>(Servlet.class).labelText("Servlet");


    @Override
    public ServletAndPath createImpl() {
        return new ServletAndPath(pathSpec.get(),servlet.instance());
    }
}

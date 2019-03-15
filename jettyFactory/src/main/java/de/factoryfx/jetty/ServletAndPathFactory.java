package de.factoryfx.jetty;

import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryPolymorphicReferenceAttribute;

import javax.servlet.Servlet;

public class ServletAndPathFactory<R extends FactoryBase<?,R>> extends SimpleFactoryBase<ServletAndPath,R> {

    public final StringAttribute pathSpec = new StringAttribute().labelText("pathSpec");
    public final FactoryPolymorphicReferenceAttribute<Servlet> servlet = new FactoryPolymorphicReferenceAttribute<>(Servlet.class).labelText("Servlet");


    @Override
    public ServletAndPath createImpl() {
        return new ServletAndPath(pathSpec.get(),servlet.instance());
    }
}

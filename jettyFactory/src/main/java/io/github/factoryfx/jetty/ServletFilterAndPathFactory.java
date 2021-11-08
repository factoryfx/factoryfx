package io.github.factoryfx.jetty;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryPolymorphicAttribute;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import jakarta.servlet.Filter;

public class ServletFilterAndPathFactory<R extends FactoryBase<?,R>> extends SimpleFactoryBase<ServletFilterAndPath,R> {

    public final StringAttribute pathSpec = new StringAttribute().labelText("pathSpec");
    public final FactoryPolymorphicAttribute<Filter> filter = new FactoryPolymorphicAttribute<Filter>().labelText("servlets");

    @Override
    protected ServletFilterAndPath createImpl() {
        return new ServletFilterAndPath(pathSpec.get(), filter.instance());
    }

    public ServletFilterAndPathFactory() {
        this.config().setDisplayTextProvider(pathSpec::get);
    }
}

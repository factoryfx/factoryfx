package io.github.factoryfx.jetty.builder;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.builder.FactoryTemplateId;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.builder.Scope;
import io.github.factoryfx.jetty.ServletAndPathFactory;
import io.github.factoryfx.jetty.ServletFilterAndPathFactory;

import javax.servlet.Filter;
import javax.servlet.Servlet;

/**
 * builder for a servlet. used internal in {@link JettyServerBuilder#withServlet(FactoryTemplateId, String, FactoryBase)}
 * @param <R> rootFactory
 */
public class ServletFilterBuilder<R extends FactoryBase<?,R>>{
    private final String pathSpec;
    private final FactoryBase<? extends Filter, R> filter;
    private final FactoryTemplateId<ServletFilterAndPathFactory<R>> templateId;

    public ServletFilterBuilder(FactoryTemplateId<ServletFilterAndPathFactory<R>> templateId, String pathSpec, FactoryBase<? extends Filter, R> filter) {
        this.pathSpec = pathSpec;
        this.filter = filter;
        this.templateId = templateId;
    }

    void build(FactoryTreeBuilder<?,R> builder){
        builder.removeFactory(templateId);
        builder.addFactory(templateId, Scope.PROTOTYPE , (ctx)->{
            ServletFilterAndPathFactory<R> servletAndPathFactory = new ServletFilterAndPathFactory<>();
            servletAndPathFactory.pathSpec.set(pathSpec);
            servletAndPathFactory.filter.set(filter);
            return servletAndPathFactory;
        });
    }

    FactoryTemplateId<ServletFilterAndPathFactory<R>> getTemplateId() {
        return this.templateId;
    }
}

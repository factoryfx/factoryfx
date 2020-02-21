package io.github.factoryfx.jetty.builder;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.builder.FactoryTemplateId;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.builder.Scope;
import io.github.factoryfx.jetty.ServletAndPathFactory;

import javax.servlet.Servlet;

/**
 * builder for a servlet. used internal in {@link JettyServerBuilder#withServlet(FactoryTemplateId, String, FactoryBase)}
 * @param <R> rootFactory
 */
public class ServletBuilder<R extends FactoryBase<?,R>>{
    private final String pathSpec;
    private final FactoryBase<? extends Servlet, R> servlet;
    private final FactoryTemplateId<ServletAndPathFactory<R>> templateId;

    public ServletBuilder(FactoryTemplateId<ServletAndPathFactory<R>> templateId, String pathSpec, FactoryBase<? extends Servlet, R> servlet) {
        this.pathSpec = pathSpec;
        this.servlet = servlet;
        this.templateId = templateId;
    }

    void build(FactoryTreeBuilder<?,R> builder){
        builder.removeFactory(templateId);
        builder.addFactory(templateId, Scope.PROTOTYPE , (ctx)->{
            ServletAndPathFactory<R> servletAndPathFactory = new ServletAndPathFactory<>();
            servletAndPathFactory.pathSpec.set(pathSpec);
            servletAndPathFactory.servlet.set(servlet);
            return servletAndPathFactory;
        });
    }

    FactoryTemplateId<ServletAndPathFactory<R>> getTemplateId() {
        return this.templateId;
    }
}

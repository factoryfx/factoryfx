package io.github.factoryfx.jetty.builder;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.builder.*;
import io.github.factoryfx.jetty.*;

/**
 * simplified constructor for JettyServerFactory as root
 */
public class SimpleJettyServerBuilder<R extends FactoryBase<?,R>> extends JettyServerBuilder<R,JettyServerFactory<R>>{

    public SimpleJettyServerBuilder(){
        super(new FactoryTemplateId<>(null, JettyServerFactory.class), JettyServerFactory::new);
    }

}

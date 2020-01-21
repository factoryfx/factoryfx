package io.github.factoryfx.jetty.builder;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.builder.FactoryTemplateId;
import io.github.factoryfx.factory.builder.Scope;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JettyFactoryTreeBuilderTest {

    @Test
    public void test_scope(){
        JettyFactoryTreeBuilder builder = new JettyFactoryTreeBuilder((jetty, ctx)->jetty
                .withHost("localhost").withPort(8005));
        builder.buildTreeUnvalidated();

        Assertions.assertEquals( Scope.SINGLETON, builder.getScope(new FactoryTemplateId<>(JettyServerRootFactory.class,"DefaultJettySetup")));
    }

}
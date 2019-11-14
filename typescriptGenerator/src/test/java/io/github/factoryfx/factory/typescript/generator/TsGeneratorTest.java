package io.github.factoryfx.factory.typescript.generator;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryListAttribute;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.typescript.generator.data.*;
import io.github.factoryfx.jetty.builder.JettyServerBuilder;
import io.github.factoryfx.jetty.JettyServerFactory;
import io.github.factoryfx.jetty.builder.SimpleJettyServerBuilder;
import org.eclipse.jetty.server.Server;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;


import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class TsGeneratorTest {


    @Test
    public void smoketest(@TempDir Path targetDir) {
        TsGenerator<ExampleData> tsClassCreator=new TsGenerator<>(targetDir, List.of(ExampleData.class, ExampleData2.class, ExampleData3.class, ExampleDataAll.class , ExampleDataIgnore.class, ExampleFactory.class));
        tsClassCreator.generateTs();

    }


    public static class TestHttpServer extends SimpleFactoryBase<Server, TestHttpServer> {
        public final FactoryAttribute<Server, JettyServerFactory<TestHttpServer>> server = new FactoryAttribute<>();

        @Override
        protected Server createImpl() {
            return server.instance();
        }
    }


    @Test
    @SuppressWarnings("unchecked")
    public void smoketest_jettyserver(@TempDir Path targetDir)  {
        FactoryTreeBuilder<Server, TestHttpServer> builder = new FactoryTreeBuilder<>(TestHttpServer.class);
        builder.addBuilder(ctx-> new SimpleJettyServerBuilder<TestHttpServer>()
                .withHost("localhost").withPort(8005));


        HashSet<Class<? extends FactoryBase<?,TestHttpServer>>> factoryClasses = new HashSet<>();
        for (FactoryBase<?, TestHttpServer> factory : builder.buildTree().internal().collectChildrenDeep()) {
            factoryClasses.add((Class<? extends FactoryBase<?, TestHttpServer>>) factory.getClass());
            factory.internal().visitAttributesFlat((attributeVariableName, attribute) -> {
                if (attribute instanceof FactoryAttribute){
                    factoryClasses.add((Class<? extends FactoryBase<?, TestHttpServer>>) ((FactoryAttribute<?,?>)attribute).internal_getReferenceClass());
                }
                if (attribute instanceof FactoryListAttribute){
                    factoryClasses.add((Class<? extends FactoryBase<?, TestHttpServer>>) ((FactoryListAttribute<?,?>)attribute).internal_getReferenceClass());
                }
            });
        }



        TsGenerator<TestHttpServer> tsClassCreator=new TsGenerator<>(targetDir, new ArrayList<>(factoryClasses));
        tsClassCreator.generateJs();
    }
}


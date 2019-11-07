package io.github.factoryfx.factory.typescript.generator.testserver;

import io.github.factoryfx.dom.rest.FilesystemStaticFileAccessFactory;
import io.github.factoryfx.dom.rest.MicroserviceDomResourceFactory;
import io.github.factoryfx.factory.attribute.types.EncryptedString;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.builder.Scope;
import io.github.factoryfx.jetty.builder.JettyServerBuilder;
import io.github.factoryfx.jetty.JettyServerFactory;
import io.github.factoryfx.jetty.builder.SimpleJettyServerBuilder;
import org.eclipse.jetty.server.Server;

import java.io.File;

public class TestServerBuilder {

    @SuppressWarnings("unchecked")
    public FactoryTreeBuilder< Server, TestServerFactory> create() {
        FactoryTreeBuilder< Server, TestServerFactory> builder = new FactoryTreeBuilder<>(TestServerFactory.class,(ctx)->{
            TestServerFactory testServerFactory = new TestServerFactory();
            testServerFactory.stringAttribute.set("1233");
            testServerFactory.server.set(ctx.get(JettyServerFactory.class));
            testServerFactory.stringListAttribute.add("1111");
            testServerFactory.stringListAttribute.add("22222");
            testServerFactory.exampleFactory.set(new ExampleFactory());
            testServerFactory.encryptedStringAttribute.set(new EncryptedString("example124", "jNNxjStGsrwgu+4G5DYc9Q=="));
            return testServerFactory;
        });
        builder.addBuilder(ctx-> new SimpleJettyServerBuilder<Server,TestServerFactory>()
                .withHost("localhost").withPort(8005).withResource(ctx.get(MicroserviceDomResourceFactory.class))
        );


        builder.addFactory(MicroserviceDomResourceFactory.class, Scope.SINGLETON, ctx->{
            MicroserviceDomResourceFactory microserviceDomResourceFactory = new MicroserviceDomResourceFactory();
            FilesystemStaticFileAccessFactory filesystemStaticFileAccessFactory = new FilesystemStaticFileAccessFactory();
            filesystemStaticFileAccessFactory.basePath.set(new File("./src/main/resources/js/").getAbsolutePath()+"/");
            microserviceDomResourceFactory.staticFileAccess.set(filesystemStaticFileAccessFactory);
            return microserviceDomResourceFactory;
        });

        return builder;
    }

}

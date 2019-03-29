package io.github.factoryfx.jetty;

import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.SimpleFactoryBase;
import org.junit.jupiter.api.Test;

public class JettyServerBuilderTest {

    public static class DummyResource extends SimpleFactoryBase<Void,DummyRoot> {

        @Override
        public Void createImpl() {
            return null;
        }
    }

    public static class DummyRoot extends JettyServerFactory<DummyRoot> {
    }


    @Test
    public void test_json(){
        JettyServerFactory<DummyRoot> serverFactory = new JettyServerBuilder<>(new DummyRoot()).withPort(123).withResource(new DummyResource()).build();

        System.out.println(ObjectMapperBuilder.build().writeValueAsString(serverFactory));

//        ObjectMapperBuilder.build().copy(serverFactory);
    }

}
package io.github.factoryfx.jetty;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.SimpleFactoryBase;
import org.junit.jupiter.api.Test;

public class JettyServerBuilderTest {

    public static class DummyResource extends SimpleFactoryBase<Void,DummyRoot> {

        @Override
        protected Void createImpl() {
            return null;
        }
    }

    public static class DummyRoot extends JettyServerFactory<DummyRoot> {
    }


    @Test
    public void test_json(){
        JettyServerFactory<DummyRoot> serverFactory = new JettyServerBuilder<DummyRoot>().withPort(123).withResource(new DummyResource()).buildTo(new DummyRoot());

        System.out.println(ObjectMapperBuilder.build().writeValueAsString(serverFactory));

//        ObjectMapperBuilder.build().copy(serverFactory);
    }

    public static  class SpecialObjectMapperFactory extends SimpleFactoryBase<ObjectMapper, DummyRoot> {
        @Override
        protected ObjectMapper createImpl() {
            return ObjectMapperBuilder.buildNewObjectMapper();
        }
    }



    @Test
    public void test_ObjectMapper(){
        new JettyServerBuilder<DummyRoot>()
                .withHost("localhost").withPort(8080)
                .withDefaultJerseyObjectMapper(new SpecialObjectMapperFactory())
                .build();



//        ObjectMapperBuilder.build().copy(serverFactory);
    }

}
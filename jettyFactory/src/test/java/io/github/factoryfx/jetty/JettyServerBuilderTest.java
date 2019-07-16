package io.github.factoryfx.jetty;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.SimpleFactoryBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.AssertionsKt$sam$i$org_junit_jupiter_api_function_Executable$0;
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
                .withHost("localhost").withPort(8087)
                .withDefaultJerseyObjectMapper(new SpecialObjectMapperFactory())
                .build();



//        ObjectMapperBuilder.build().copy(serverFactory);
    }

    @Test
    public void test_removeDefaultJerseyServlet_avoid_accidentally_resource_remove(){

        Assertions.assertThrows(IllegalStateException.class,()->{
                    new JettyServerBuilder<DummyRoot>()
                            .withHost("localhost").withPort(8087)
                            .withResource(new DummyResource())
                            .removeDefaultJerseyServlet()
                            .build();
                });
    }

    @Test
    public void test_removeDefaultJerseyServlet_after_removeDefaultJerseyServlet(){

        Assertions.assertThrows(IllegalStateException.class,()->{
            new JettyServerBuilder<DummyRoot>()
                    .withHost("localhost").withPort(8087)
                    .removeDefaultJerseyServlet()
                    .withResource(new DummyResource())
                    .build();
        });
    }

}
package de.factoryfx.jetty;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.factory.SimpleFactoryBase;
import org.junit.jupiter.api.Test;

public class JettyServerBuilderTest {

    public static class DummyResource extends SimpleFactoryBase<Void,Void,DummyRoot> {

        @Override
        public Void createImpl() {
            return null;
        }
    }

    public static class DummyRoot extends JettyServerFactory<Void,DummyRoot> {
    }


    @Test
    public void test_json(){
        JettyServerFactory<Void, DummyRoot> serverFactory = new JettyServerBuilder<>(new DummyRoot()).widthPort(123).withResource(new DummyResource()).build();

        System.out.println(ObjectMapperBuilder.build().writeValueAsString(serverFactory));

//        ObjectMapperBuilder.build().copy(serverFactory);
    }

}
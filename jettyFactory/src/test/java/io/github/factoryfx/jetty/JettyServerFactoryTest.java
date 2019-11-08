package io.github.factoryfx.jetty;

import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JettyServerFactoryTest {

    @Test
    public void test_json(){
        JettyServerFactory test = new JettyServerFactory();
        ObjectMapperBuilder.build().copy(test);
    }

}
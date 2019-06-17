package io.github.factoryfx.factory;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SimpleFactoryBaseTest {

    public static class InvalidSimpleFactoryFactory extends SimpleFactoryBase<Void,InvalidSimpleFactoryFactory>{

        public InvalidSimpleFactoryFactory(){
            this.configLifeCycle().setCreator(() -> null);
        }

        @Override
        protected Void createImpl() {
            return null;
        }
    }

    @Test
    public void test_invalid_creator_set(){
        Assertions.assertThrows(IllegalStateException.class, () -> {
            //creator should not work cause inheritance api in SimpleFactory
            InvalidSimpleFactoryFactory simpleFactoryFactory = new InvalidSimpleFactoryFactory();
        });
    }

}
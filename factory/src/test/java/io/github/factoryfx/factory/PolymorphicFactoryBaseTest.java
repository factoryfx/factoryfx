package io.github.factoryfx.factory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class PolymorphicFactoryBaseTest {

    public static class InvalidPolymorphicFactoryBase extends PolymorphicFactoryBase<Void,SimpleFactoryBaseTest.InvalidSimpleFactoryFactory>{

        public InvalidPolymorphicFactoryBase(){
            this.configLifeCycle().setCreator(() -> null);
        }

        @Override
        public Void createImpl() {
            return null;
        }
    }

    @Test
    public void test_invalid_creator_set(){
        //creator should not work cause inheritance api in SimpleFactory
        Assertions.assertThrows(IllegalStateException.class, () -> {
            InvalidPolymorphicFactoryBase simpleFactoryFactory = new InvalidPolymorphicFactoryBase();
        });
    }

}
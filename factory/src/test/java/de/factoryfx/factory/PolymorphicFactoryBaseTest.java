package de.factoryfx.factory;

import org.junit.Test;

public class PolymorphicFactoryBaseTest {

    public static class InvalidPolymorphicFactoryBase extends PolymorphicFactoryBase<Void,Void,SimpleFactoryBaseTest.InvalidSimpleFactoryFactory>{

        public InvalidPolymorphicFactoryBase(){
            this.configLifeCycle().setCreator(() -> null);
        }

        @Override
        public Void createImpl() {
            return null;
        }
    }

    @Test(expected = IllegalStateException.class)
    public void test_invalid_creator_set(){
        //creator should not work cause inheritance api in SimpleFactory
        InvalidPolymorphicFactoryBase simpleFactoryFactory = new InvalidPolymorphicFactoryBase();
    }

}
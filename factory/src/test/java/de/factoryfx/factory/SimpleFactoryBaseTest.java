package de.factoryfx.factory;

import org.junit.Test;

public class SimpleFactoryBaseTest {

    public static class InvalidSimpleFactoryFactory extends SimpleFactoryBase<Void,Void,InvalidSimpleFactoryFactory>{

        public InvalidSimpleFactoryFactory(){
            this.configLiveCycle().setCreator(() -> null);
        }

        @Override
        public Void createImpl() {
            return null;
        }
    }

    @Test(expected = IllegalStateException.class)
    public void test_invalid_creator_set(){
        //creator should not work cause inheritance api in SimpleFactory
        InvalidSimpleFactoryFactory simpleFactoryFactory = new InvalidSimpleFactoryFactory();
    }

}
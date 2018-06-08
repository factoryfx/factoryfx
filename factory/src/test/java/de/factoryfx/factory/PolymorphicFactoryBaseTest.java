package de.factoryfx.factory;

import org.junit.Test;

import java.util.function.Supplier;

import static org.junit.Assert.*;

public class PolymorphicFactoryBaseTest {

    public static class InvalidPolymorphicFactoryBase extends PolymorphicFactoryBase<Void,Void,SimpleFactoryBaseTest.InvalidSimpleFactoryFactory>{

        public InvalidPolymorphicFactoryBase(){
            this.configLiveCycle().setCreator(new Supplier<Void>() {
                @Override
                public Void get() {
                    return null;
                }
            });
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
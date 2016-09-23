package de.factoryfx.factory.validator;

import de.factoryfx.factory.testfactories.ExampleFactoryA;
import de.factoryfx.factory.testfactories.ExampleFactoryB;
import org.junit.Assert;
import org.junit.Test;

public class ExampleModelTest {

    @Test
    public void test_ExampleA(){
        Assert.assertEquals("",new FactoryStyleValidator().validateFactory(new ExampleFactoryA()).orElse(""));
    }

    @Test
    public void test_ExampleB(){
        Assert.assertEquals("",new FactoryStyleValidator().validateFactory(new ExampleFactoryB()).orElse(""));
    }

}

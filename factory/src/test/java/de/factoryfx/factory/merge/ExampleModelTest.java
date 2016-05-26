package de.factoryfx.factory.merge;

import de.factoryfx.factory.validator.FactoryValidator;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import de.factoryfx.factory.testfactories.ExampleFactoryB;
import org.junit.Assert;
import org.junit.Test;

public class ExampleModelTest {

    @Test
    public void test_ExampleA(){
        Assert.assertEquals("",new FactoryValidator().validateModel(new ExampleFactoryA()).orElse(""));
    }

    @Test
    public void test_ExampleB(){
        Assert.assertEquals("",new FactoryValidator().validateModel(new ExampleFactoryB()).orElse(""));
    }

}

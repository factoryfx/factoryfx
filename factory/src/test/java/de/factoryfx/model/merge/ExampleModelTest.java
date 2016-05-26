package de.factoryfx.model.merge;

import de.factoryfx.model.validator.FactoryValidator;
import de.factoryfx.model.testfactories.ExampleFactoryA;
import de.factoryfx.model.testfactories.ExampleFactoryB;
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

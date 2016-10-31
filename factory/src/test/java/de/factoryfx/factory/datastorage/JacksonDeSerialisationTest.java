package de.factoryfx.factory.datastorage;

import de.factoryfx.factory.testfactories.ExampleFactoryA;
import org.junit.Assert;
import org.junit.Test;

public class JacksonDeSerialisationTest {

    @Test
    public void test_canRead(){
        JacksonDeSerialisation<ExampleFactoryA> jacksonDeSerialisation = new JacksonDeSerialisation<>(ExampleFactoryA.class,1);
        Assert.assertTrue(jacksonDeSerialisation.canRead("",1));

    }

    @Test
    public void test_canRead_invalid(){

        JacksonDeSerialisation<ExampleFactoryA> jacksonDeSerialisation = new JacksonDeSerialisation<>(ExampleFactoryA.class,1);
        Assert.assertFalse(jacksonDeSerialisation.canRead("",2));

    }

}
package de.factoryfx.data.storage;

import de.factoryfx.data.merge.testdata.ExampleDataA;
import org.junit.Assert;
import org.junit.Test;

public class JacksonDeSerialisationTest {

    @Test
    public void test_canRead(){
        JacksonDeSerialisation<ExampleDataA,Void> jacksonDeSerialisation = new JacksonDeSerialisation<>(ExampleDataA.class,1);
        Assert.assertTrue(jacksonDeSerialisation.canRead(1));

    }

    @Test
    public void test_canRead_invalid(){

        JacksonDeSerialisation<ExampleDataA,Void> jacksonDeSerialisation = new JacksonDeSerialisation<>(ExampleDataA.class,1);
        Assert.assertFalse(jacksonDeSerialisation.canRead(2));

    }

}
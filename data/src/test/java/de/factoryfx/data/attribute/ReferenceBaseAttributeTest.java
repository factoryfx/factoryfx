package de.factoryfx.data.attribute;

import de.factoryfx.data.Data;
import org.junit.Test;

public class ReferenceBaseAttributeTest {

    public static class GenericData<T> extends Data {

    }

    @Test
    public void test_setupunsafe(){
        DataReferenceAttribute<GenericData<String>> test1 = new DataReferenceAttribute<>(GenericData.class,null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_setupunsafe_wrongclass(){
        DataReferenceAttribute<GenericData<String>> test1 = new DataReferenceAttribute<>(Object.class,null);
    }

}
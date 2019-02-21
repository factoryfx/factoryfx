package de.factoryfx.data.attribute;

import de.factoryfx.data.Data;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ReferenceBaseAttributeTest {

    public static class GenericData<T> extends Data {

    }

    @Test
    public void test_setupunsafe(){
        DataReferenceAttribute<GenericData<String>> test1 = new DataReferenceAttribute<>(GenericData.class,null);
    }

    @Test
    public void test_setupunsafe_wrongclass(){
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            DataReferenceAttribute<GenericData<String>> test1 = new DataReferenceAttribute<>(Object.class, null);
        });
    }

}
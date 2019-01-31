package de.factoryfx.data.storage.migration.metadata;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.storage.migration.GeneralStorageFormat;
import org.junit.Test;

import static org.junit.Assert.*;

public class GeneralStorageFormatTest {
    @Test
    public void test_json(){
        GeneralStorageFormat value=new GeneralStorageFormat(1,2);
        GeneralStorageFormat copy = ObjectMapperBuilder.build().copy(value);

        assertTrue(copy.match(new GeneralStorageFormat(1, 2)));

    }
}
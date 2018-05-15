package de.factoryfx.data.attribute;

import de.factoryfx.data.merge.testfactories.ExampleDataA;
import org.junit.Test;

public class ReferenceBaseAttributeTest {

    @Test
    public void test_setupunsafe(){
        DataReferenceAttribute<ExampleDataA> test1 = new DataReferenceAttribute<ExampleDataA>().setupUnsafe(ExampleDataA.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_setupunsafe_wrongclass(){
        DataReferenceAttribute<ExampleDataA> test1 = new DataReferenceAttribute<ExampleDataA>().setupUnsafe(Object.class);
    }

}
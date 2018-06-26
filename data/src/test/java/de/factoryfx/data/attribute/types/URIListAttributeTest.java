package de.factoryfx.data.attribute.types;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.Assert;
import org.junit.Test;

public class URIListAttributeTest {

    @Test
    public void test_json(){
        URIListAttribute attribute = new URIListAttribute();
        attribute.addUnchecked("www.google.de");
        URIListAttribute copy = ObjectMapperBuilder.build().copy(attribute);
        Assert.assertEquals("www.google.de",copy.get().iterator().next().toString());
    }

}
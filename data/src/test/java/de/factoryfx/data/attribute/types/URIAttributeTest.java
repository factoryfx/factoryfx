package de.factoryfx.data.attribute.types;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.Assert;
import org.junit.Test;

public class URIAttributeTest {

    @Test
    public void test_json(){
        URIAttribute attribute = new URIAttribute();
        attribute.setUnchecked("www.google.de");
        URIAttribute copy = ObjectMapperBuilder.build().copy(attribute);
        Assert.assertEquals("www.google.de",copy.get().toString());
    }

}
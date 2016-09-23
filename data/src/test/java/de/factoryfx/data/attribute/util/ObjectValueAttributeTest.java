package de.factoryfx.data.attribute.util;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.Assert;
import org.junit.Test;

public class ObjectValueAttributeTest {

    public static class NoJsonPropertiesObject{
            //dummy for unseriliasable objetc
    }

    public abstract class NoJsonPropertiesObjectAbstract {
        //dummy for unseriliasable objetc
        public String test="sfsfdsf";
    }

    @Test
    public void test_json(){

        ObjectValueAttribute<NoJsonPropertiesObject> attribute= new ObjectValueAttribute<>(new AttributeMetadata());
        attribute.set(new NoJsonPropertiesObject());

        ObjectValueAttribute<NoJsonPropertiesObject>  copy= ObjectMapperBuilder.build().copy(attribute);
        Assert.assertNull(copy);
    }

    @Test
    public void test_json_abstract(){

        ObjectValueAttribute<NoJsonPropertiesObjectAbstract> attribute= new ObjectValueAttribute<>(new AttributeMetadata());
        attribute.set(new NoJsonPropertiesObjectAbstract(){});

        ObjectValueAttribute<NoJsonPropertiesObjectAbstract>  copy= ObjectMapperBuilder.build().copy(attribute);
        Assert.assertNull(copy);
    }

    @Test
    public void test_json_deserialseabstract(){

        ObjectValueAttribute<NoJsonPropertiesObjectAbstract> attribute= new ObjectValueAttribute<>(new AttributeMetadata());
        attribute.set(new NoJsonPropertiesObjectAbstract(){});

        String value = ObjectMapperBuilder.build().writeValueAsString(attribute);
        System.out.println("fhg"+value);
    }
}
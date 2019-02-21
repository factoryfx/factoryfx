package de.factoryfx.data.attribute.types;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ObjectValueAttributeTest {

    public static class ExampleObjectValueData {
        public final ObjectValueAttribute<NoJsonPropertiesObject> attribute = new ObjectValueAttribute<>();
    }

    public class NoJsonPropertiesObject {
        //dummy for not serializable object
        public String test="sfsfdsf";
    }

    @Test
    public void test_json_inside_data(){
        ExampleObjectValueData noJsonPropertiesObject = new ExampleObjectValueData();
        noJsonPropertiesObject.attribute.set(new NoJsonPropertiesObject());

        ExampleObjectValueData copy= ObjectMapperBuilder.build().copy(noJsonPropertiesObject);
        Assertions.assertNull(copy.attribute.get());
    }

    @Test
    public void test_json(){

        ObjectValueAttribute<NoJsonPropertiesObject> attribute= new ObjectValueAttribute<>();
        attribute.set(new NoJsonPropertiesObject(){});

        ObjectValueAttribute<NoJsonPropertiesObject>  copy= ObjectMapperBuilder.build().copy(attribute);
    }

}
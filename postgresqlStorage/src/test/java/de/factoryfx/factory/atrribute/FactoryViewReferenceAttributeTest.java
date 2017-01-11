package de.factoryfx.factory.atrribute;

import java.util.function.Function;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.Test;

public class FactoryViewReferenceAttributeTest {

    @Test
    public void test_jon(){
        FactoryViewReferenceAttribute attribute = new FactoryViewReferenceAttribute(new AttributeMetadata(), new Function() {
            @Override
            public Object apply(Object o) {
                return "gfhgf";
            }
        });

        ObjectMapperBuilder.build().copy(attribute);
    }

}
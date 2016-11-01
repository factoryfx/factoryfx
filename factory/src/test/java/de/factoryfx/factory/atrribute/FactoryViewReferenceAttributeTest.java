package de.factoryfx.factory.atrribute;

import java.util.function.BiFunction;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.Test;

public class FactoryViewReferenceAttributeTest {

    @Test
    public void test_jon(){
        FactoryViewReferenceAttribute attribute = new FactoryViewReferenceAttribute(new AttributeMetadata(), new BiFunction() {
            @Override
            public Object apply(Object o, Object o2) {
                return "gfhgf";
            }
        });

        ObjectMapperBuilder.build().copy(attribute);
    }

}
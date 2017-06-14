package de.factoryfx.factory.atrribute;

import java.util.function.Function;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.Test;

public class FactoryViewReferenceAttributeTest {

    @Test
    @SuppressWarnings("unchecked")
    public void test_jon(){
        FactoryViewReferenceAttribute attribute = new FactoryViewReferenceAttribute(new Function() {
            @Override
            public Object apply(Object o) {
                return "gfhgf";
            }
        });

        ObjectMapperBuilder.build().copy(attribute);
    }

}
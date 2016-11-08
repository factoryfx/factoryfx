package de.factoryfx.factory.atrribute;

import java.util.ArrayList;
import java.util.function.BiFunction;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.Test;

public class FactoryViewListReferenceAttributeTest {
    @Test
    public void test_jon(){
        FactoryViewListReferenceAttribute attribute = new FactoryViewListReferenceAttribute(new AttributeMetadata(), new BiFunction() {
            @Override
            public Object apply(Object o, Object o2) {
                ArrayList<String> strings = new ArrayList<>();
                strings.add("gfhgf");
                return strings;
            }
        });

        ObjectMapperBuilder.build().copy(attribute);
        System.out.println(ObjectMapperBuilder.build().writeValueAsString(attribute));
    }
}
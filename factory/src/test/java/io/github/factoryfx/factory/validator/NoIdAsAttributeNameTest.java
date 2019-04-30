package io.github.factoryfx.factory.validator;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;


class NoIdAsAttributeNameTest {
    private static class NotIdFactory extends FactoryBase<Void, NotIdFactory> {
        public final StringAttribute id = new StringAttribute();
    }

    @Test
    public void test() throws NoSuchFieldException {
        Optional<String> result = new NoIdAsAttributeName(NotIdFactory.class.getField("id")).validateFactory();
        Assertions.assertTrue(result.isPresent());
    }

}
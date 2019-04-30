package io.github.factoryfx.factory.validator;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.Optional;


class LocaleAttributeValidationTest {
    private static class LocaleFactory extends FactoryBase<Void, LocaleFactory> {
        public final StringAttribute stringAttribute = new StringAttribute().de("adfhd");
    }

    private static class LocaleBrokenFactory extends FactoryBase<Void, LocaleFactory> {
        public final StringAttribute stringAttribute = new StringAttribute();
    }

    @Test
    public void test() throws NoSuchFieldException {
        Optional<String> result = new LocaleAttributeValidation(new LocaleBrokenFactory(), LocaleBrokenFactory.class.getField("stringAttribute"), Locale.GERMAN).validateFactory();
        Assertions.assertTrue(result.isPresent());
    }

    @Test
    public void test_negative() throws NoSuchFieldException {
        Optional<String> result = new LocaleAttributeValidation(new LocaleFactory(),LocaleFactory.class.getField("stringAttribute"), Locale.GERMAN).validateFactory();
        Assertions.assertFalse(result.isPresent());
    }
}
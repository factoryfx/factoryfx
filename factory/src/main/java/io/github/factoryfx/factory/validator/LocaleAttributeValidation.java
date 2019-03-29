package io.github.factoryfx.factory.validator;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Optional;

import com.google.common.base.Strings;
import io.github.factoryfx.factory.attribute.Attribute;
import io.github.factoryfx.factory.FactoryBase;

public class LocaleAttributeValidation implements FactoryStyleValidation {
    private final FactoryBase<?,?> factoryBase;
    private final Field attributeField;
    private Locale locale;

    public LocaleAttributeValidation(FactoryBase<?,?> factoryBase, Field attributeField, Locale locale) {
        this.factoryBase = factoryBase;
        this.attributeField = attributeField;
        this.locale = locale;
    }

    @Override
    public Optional<String> validateFactory() {
        try {
            if (Strings.isNullOrEmpty(((Attribute<?, ?>) attributeField.get(factoryBase)).internal_getPreferredLabelText(locale))) {
                return Optional.of("locale: '" + locale + "' should be set: " + factoryBase.getClass().getName() + "#" + attributeField.getName());
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }
}

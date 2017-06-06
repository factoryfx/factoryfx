package de.factoryfx.factory.validator;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Optional;

import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.factory.FactoryBase;

public class LocaleAttributeValidation implements FactoryStyleValidation {
    private final FactoryBase<?, ?> factoryBase;
    private final Field attributeField;
    private Locale locale;

    public LocaleAttributeValidation(FactoryBase<?, ?> factoryBase, Field attributeField, Locale locale) {
        this.factoryBase = factoryBase;
        this.attributeField = attributeField;
        this.locale = locale;
    }

    @Override
    public Optional<String> validateFactory() {
        try {
            if (((Attribute<?>) attributeField.get(factoryBase)).getPreferredLabelText(locale) == null) {
                return Optional.of("locale: '" + locale + "' should be set: " + factoryBase.getClass().getName() + "#" + attributeField.getName());
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }
}

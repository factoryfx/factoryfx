package io.github.factoryfx.factory.metadata;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.Attribute;
import io.github.factoryfx.factory.util.LanguageText;

public class AttributeMetadata {

    public final String attributeVariableName;
    public final Class<? extends Attribute<?,?>> attributeClass;
    public final Class<? extends FactoryBase<?,?>> referenceClass;
    public final Class<? extends Enum<?>> enumClass;
    public final LanguageText labelText;
    public final boolean required;

    public AttributeMetadata(String attributeVariableName, Class<? extends Attribute<?, ?>> attributeClass, Class<? extends FactoryBase<?, ?>> referenceClass, Class<? extends Enum<?>> enumClass, LanguageText labelText, boolean required) {
        this.attributeVariableName = attributeVariableName;
        this.attributeClass = attributeClass;
        this.referenceClass = referenceClass;
        this.enumClass = enumClass;
        this.labelText = labelText;
        this.required = required;
    }
}

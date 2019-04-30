package io.github.factoryfx.factory.attribute;

import io.github.factoryfx.factory.util.LanguageText;

import java.util.List;

/**
 * used for editing (usually to group attributes in tabs tabs)
 */
public class AttributeGroup {
    public final List<Attribute<?,?>> group;
    public final LanguageText title;

    public AttributeGroup(LanguageText title, List<Attribute<?, ?>> group) {
        this.group = group;
        this.title = title;
    }

    public AttributeGroup(String title, List<Attribute<?, ?>> group) {
        this.group = group;
        this.title = new LanguageText().en(title);
    }
}

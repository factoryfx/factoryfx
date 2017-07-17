package de.factoryfx.data.attribute;

import de.factoryfx.data.util.LanguageText;

import java.util.ArrayList;
import java.util.List;

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

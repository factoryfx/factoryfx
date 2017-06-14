package de.factoryfx.data;

import de.factoryfx.data.attribute.Attribute;

public class AttributeAndName {
    public final Attribute<?,?> attribute;
    public final String name;

    public AttributeAndName(Attribute<?,?> attribute, String name) {
        this.attribute = attribute;
        this.name = name;
    }
}

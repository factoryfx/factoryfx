package de.factoryfx.data.attribute;

public interface AttributeVisitor {
    void value(Attribute<?,?> value);
    void reference(ReferenceAttribute<?,?> reference);
    void referenceList(ReferenceListAttribute<?,?> referenceList);
}

package de.factoryfx.data.attribute;

public interface AttributeChangeListener<T> {
    /**
     * @param attribute changed attribute
     * @param value new value
     */
    void changed(Attribute<T> attribute, T value);
    /** for weaklisteners*/
    default AttributeChangeListener<T> unwrap(){
        return this;
    }
}

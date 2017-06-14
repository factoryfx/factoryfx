package de.factoryfx.data.attribute;

public interface AttributeChangeListener<T,A extends Attribute<T,A>> {
    /**
     * @param attribute changed attribute
     * @param value new value
     */
    void changed(Attribute<T,A> attribute, T value);
    /** for weaklisteners*/
    default AttributeChangeListener<T,A> unwrap(){
        return this;
    }
}

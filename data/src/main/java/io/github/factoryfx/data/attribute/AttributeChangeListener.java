package io.github.factoryfx.data.attribute;

public interface AttributeChangeListener<T,A extends Attribute<T,A>> {
    /**
     * @param attribute changed attribute
     * @param value new value
     */
    void changed(Attribute<T,A> attribute, T value);
    /** for weak listeners
     * @return the listener*/
    default AttributeChangeListener<T,A> unwrap(){
        return this;
    }
}

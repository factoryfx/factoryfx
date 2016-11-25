package de.factoryfx.data.attribute;

import java.lang.ref.WeakReference;

public class WeakAttributeChangeListener<T> implements AttributeChangeListener<T> {

    private final WeakReference<AttributeChangeListener<T>> attributeChangeListener;

    public WeakAttributeChangeListener(AttributeChangeListener<T> attributeChangeListener) {
        this.attributeChangeListener = new WeakReference<>(attributeChangeListener);
    }

    @Override
    public void changed(Attribute<T> attribute, T value) {
        final AttributeChangeListener<T> listener = attributeChangeListener.get();
        if (listener!=null){
            listener.changed(attribute,value);
        }
    }

    @Override
    public AttributeChangeListener<T> unwrap() {
        return attributeChangeListener.get();
    }
}

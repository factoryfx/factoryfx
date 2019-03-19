package io.github.factoryfx.data.attribute;

import java.lang.ref.WeakReference;

public class WeakAttributeChangeListener<T,A extends Attribute<T,A>> implements AttributeChangeListener<T,A> {

    private final WeakReference<AttributeChangeListener<T,A>> attributeChangeListener;

    public WeakAttributeChangeListener(AttributeChangeListener<T,A> attributeChangeListener) {
        this.attributeChangeListener = new WeakReference<>(attributeChangeListener);
    }

    @Override
    public void changed(Attribute<T,A> attribute, T value) {
        final AttributeChangeListener<T,A> listener = attributeChangeListener.get();
        if (listener!=null){
            listener.changed(attribute,value);
        }
    }

    @Override
    public AttributeChangeListener<T,A> unwrap() {
        return attributeChangeListener.get();
    }
}

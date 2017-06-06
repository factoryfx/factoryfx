package de.factoryfx.data.attribute;

import de.factoryfx.data.attribute.types.StringAttribute;
import org.junit.Assert;
import org.junit.Test;

public class ImmutableValueAttributeTest {




    @Test
    public void removeListener() throws Exception {
        ImmutableValueAttribute<String> valueAttribute = new StringAttribute(new AttributeMetadata());

        final AttributeChangeListener<String> stringAttributeChangeListener = (attribute, value) -> System.out.println(value);
        valueAttribute.internal_addListener(stringAttributeChangeListener);
        Assert.assertTrue(valueAttribute.listeners.size()==1);
        valueAttribute.internal_removeListener(stringAttributeChangeListener);
        Assert.assertTrue(valueAttribute.listeners.size()==0);
    }

    @Test
    public void removeWeakListener() throws Exception {
        ImmutableValueAttribute<String> valueAttribute = new StringAttribute(new AttributeMetadata());

        final AttributeChangeListener<String> stringAttributeChangeListener = (attribute, value) -> System.out.println(value);
        valueAttribute.internal_addListener(new WeakAttributeChangeListener<>(stringAttributeChangeListener));
        Assert.assertTrue(valueAttribute.listeners.size()==1);
        valueAttribute.internal_removeListener(stringAttributeChangeListener);
        Assert.assertTrue(valueAttribute.listeners.size()==0);
    }

    @Test
    public void removeWeakListener_after_gc() throws Exception {
        ImmutableValueAttribute<String> valueAttribute = new StringAttribute(new AttributeMetadata());

        final AttributeChangeListener<String> stringAttributeChangeListener = (attribute, value) -> System.out.println(value);
        valueAttribute.internal_addListener(new WeakAttributeChangeListener<>(null));
        Assert.assertTrue(valueAttribute.listeners.size()==1);
        valueAttribute.internal_removeListener(stringAttributeChangeListener);
        Assert.assertTrue(valueAttribute.listeners.size()==0);
    }


}
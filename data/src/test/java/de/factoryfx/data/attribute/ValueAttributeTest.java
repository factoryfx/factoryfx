package de.factoryfx.data.attribute;

import org.junit.Assert;
import org.junit.Test;

public class ValueAttributeTest {




    @Test
    public void removeListener() throws Exception {
        ValueAttribute<String> valueAttribute = new ValueAttribute<>(new AttributeMetadata(),String.class);

        final AttributeChangeListener<String> stringAttributeChangeListener = (attribute, value) -> System.out.println(value);
        valueAttribute.addListener(stringAttributeChangeListener);
        Assert.assertTrue(valueAttribute.listeners.size()==1);
        valueAttribute.removeListener(stringAttributeChangeListener);
        Assert.assertTrue(valueAttribute.listeners.size()==0);
    }

    @Test
    public void removeWeakListener() throws Exception {
        ValueAttribute<String> valueAttribute = new ValueAttribute<>(new AttributeMetadata(),String.class);

        final AttributeChangeListener<String> stringAttributeChangeListener = (attribute, value) -> System.out.println(value);
        valueAttribute.addListener(new WeakAttributeChangeListener<>(stringAttributeChangeListener));
        Assert.assertTrue(valueAttribute.listeners.size()==1);
        valueAttribute.removeListener(stringAttributeChangeListener);
        Assert.assertTrue(valueAttribute.listeners.size()==0);
    }


}
package de.factoryfx.data.attribute;

import de.factoryfx.data.attribute.primitive.IntegerAttribute;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.data.validation.ValidationError;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class ImmutableValueAttributeTest {

    @Test
    public void removeListener() throws Exception {
        ImmutableValueAttribute<String,StringAttribute> valueAttribute = new StringAttribute();

        final AttributeChangeListener<String,StringAttribute> stringAttributeChangeListener = (attribute, value) -> System.out.println(value);
        valueAttribute.internal_addListener(stringAttributeChangeListener);
        Assert.assertTrue(valueAttribute.listeners.size()==1);
        valueAttribute.internal_removeListener(stringAttributeChangeListener);
        Assert.assertTrue(valueAttribute.listeners.size()==0);
    }

    @Test
    public void removeWeakListener() throws Exception {
        ImmutableValueAttribute<String,StringAttribute> valueAttribute = new StringAttribute();

        final AttributeChangeListener<String,StringAttribute> stringAttributeChangeListener = (attribute, value) -> System.out.println(value);
        valueAttribute.internal_addListener(new WeakAttributeChangeListener<>(stringAttributeChangeListener));
        Assert.assertTrue(valueAttribute.listeners.size()==1);
        valueAttribute.internal_removeListener(stringAttributeChangeListener);
        Assert.assertTrue(valueAttribute.listeners.size()==0);
    }

    @Test
    public void removeWeakListener_after_gc() throws Exception {
        ImmutableValueAttribute<String,StringAttribute> valueAttribute = new StringAttribute();

        final AttributeChangeListener<String,StringAttribute> stringAttributeChangeListener = (attribute, value) -> System.out.println(value);
        valueAttribute.internal_addListener(new WeakAttributeChangeListener<>(null));
        Assert.assertTrue(valueAttribute.listeners.size()==1);
        valueAttribute.internal_removeListener(stringAttributeChangeListener);
        Assert.assertTrue(valueAttribute.listeners.size()==0);
    }

    @Test
    public void test_match() throws Exception {
        StringAttribute stringAttribute = new StringAttribute();
        stringAttribute.set("123");
        Assert.assertTrue(stringAttribute.match("123"));
    }

    @Test
    public void test_match_attribute() throws Exception {
        StringAttribute stringAttribute = new StringAttribute();
        stringAttribute.set("123");

        StringAttribute stringAttribute2 = new StringAttribute();
        stringAttribute2.set("123");
        Assert.assertTrue(stringAttribute.match(stringAttribute2));
    }

    @Test
    public void test_null(){
        IntegerAttribute attribute = new IntegerAttribute();

        {
            attribute.set(null);
            List<ValidationError> validationErrors = attribute.internal_validate(null,"");
            Assert.assertEquals(1, validationErrors.size());
        }

        {
            attribute.set(1);
            List<ValidationError> validationErrors = attribute.internal_validate(null,"");
            Assert.assertEquals(0, validationErrors.size());
        }
    }

    @Test
    public void test_nullable(){
        IntegerAttribute attribute = new IntegerAttribute().nullable();

        {
            attribute.set(null);
            List<ValidationError> validationErrors = attribute.internal_validate(null,"");
            Assert.assertEquals(0, validationErrors.size());
        }

        {
            attribute.set(1);
            List<ValidationError> validationErrors = attribute.internal_validate(null,"");
            Assert.assertEquals(0, validationErrors.size());
        }
    }

    @Test
    public void test_internal_require_true(){
        IntegerAttribute attribute = new IntegerAttribute();
        Assert.assertTrue(attribute.internal_required());
    }

    @Test
    public void test_internal_require_false(){
        IntegerAttribute attribute = new IntegerAttribute().nullable();
        Assert.assertFalse(attribute.internal_required());
    }

}
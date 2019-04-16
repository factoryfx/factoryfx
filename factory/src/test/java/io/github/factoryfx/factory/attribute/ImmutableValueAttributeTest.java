package io.github.factoryfx.factory.attribute;

import io.github.factoryfx.factory.attribute.primitive.IntegerAttribute;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.validation.ValidationError;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ImmutableValueAttributeTest {

    @Test
    public void removeListener()  {
        ImmutableValueAttribute<String,StringAttribute> valueAttribute = new StringAttribute();

        final AttributeChangeListener<String,StringAttribute> stringAttributeChangeListener = (attribute, value) -> System.out.println(value);
        valueAttribute.internal_addListener(stringAttributeChangeListener);
        Assertions.assertTrue(valueAttribute.internal_getListeners().size()==1);
        valueAttribute.internal_removeListener(stringAttributeChangeListener);
        Assertions.assertTrue(valueAttribute.internal_getListeners().size()==0);
    }

    @Test
    public void removeWeakListener() {
        ImmutableValueAttribute<String,StringAttribute> valueAttribute = new StringAttribute();

        final AttributeChangeListener<String,StringAttribute> stringAttributeChangeListener = (attribute, value) -> System.out.println(value);
        valueAttribute.internal_addListener(new WeakAttributeChangeListener<>(stringAttributeChangeListener));
        Assertions.assertTrue(valueAttribute.internal_getListeners().size()==1);
        valueAttribute.internal_removeListener(stringAttributeChangeListener);
        Assertions.assertTrue(valueAttribute.internal_getListeners().size()==0);
    }

    @Test
    public void removeWeakListener_after_gc() {
        ImmutableValueAttribute<String,StringAttribute> valueAttribute = new StringAttribute();

        final AttributeChangeListener<String,StringAttribute> stringAttributeChangeListener = (attribute, value) -> System.out.println(value);
        valueAttribute.internal_addListener(new WeakAttributeChangeListener<>(null));
        Assertions.assertTrue(valueAttribute.internal_getListeners().size()==1);
        valueAttribute.internal_removeListener(stringAttributeChangeListener);
        Assertions.assertTrue(valueAttribute.internal_getListeners().size()==0);
    }

    @Test
    public void test_match() {
        StringAttribute stringAttribute = new StringAttribute();
        stringAttribute.set("123");
        Assertions.assertTrue(stringAttribute.match("123"));
    }

    @Test
    public void test_match_attribute() {
        StringAttribute stringAttribute = new StringAttribute();
        stringAttribute.set("123");

        StringAttribute stringAttribute2 = new StringAttribute();
        stringAttribute2.set("123");
        Assertions.assertTrue(stringAttribute.match(stringAttribute2));
    }

    @Test
    public void test_null(){
        IntegerAttribute attribute = new IntegerAttribute();

        {
            attribute.set(null);
            List<ValidationError> validationErrors = attribute.internal_validate(null,"");
            Assertions.assertEquals(1, validationErrors.size());
        }

        {
            attribute.set(1);
            List<ValidationError> validationErrors = attribute.internal_validate(null,"");
            Assertions.assertEquals(0, validationErrors.size());
        }
    }

    @Test
    public void test_nullable(){
        IntegerAttribute attribute = new IntegerAttribute().nullable();

        {
            attribute.set(null);
            List<ValidationError> validationErrors = attribute.internal_validate(null,"");
            Assertions.assertEquals(0, validationErrors.size());
        }

        {
            attribute.set(1);
            List<ValidationError> validationErrors = attribute.internal_validate(null,"");
            Assertions.assertEquals(0, validationErrors.size());
        }
    }

    @Test
    public void test_internal_require_true(){
        IntegerAttribute attribute = new IntegerAttribute();
        Assertions.assertTrue(attribute.internal_required());
    }

    @Test
    public void test_internal_require_false(){
        IntegerAttribute attribute = new IntegerAttribute().nullable();
        Assertions.assertFalse(attribute.internal_required());
    }

}
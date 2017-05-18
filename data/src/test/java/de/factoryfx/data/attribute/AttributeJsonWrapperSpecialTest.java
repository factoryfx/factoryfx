package de.factoryfx.data.attribute;

import com.google.common.reflect.ClassPath;
import de.factoryfx.data.attribute.types.EnumAttribute;
import de.factoryfx.data.attribute.types.WrappingValueAttribute;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.merge.testfactories.ExampleFactoryA;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class AttributeJsonWrapperSpecialTest {


    @Test
    public void test_ref_List() {
        ReferenceListAttribute<ExampleFactoryA> referenceListAttribute= new ReferenceListAttribute<>(new AttributeMetadata(),ExampleFactoryA.class);
        ExampleFactoryA value = new ExampleFactoryA();
        value.stringAttribute.set("XXX");
        referenceListAttribute.add(value);

        final AttributeJsonWrapper copy = ObjectMapperBuilder.build().copy(new AttributeJsonWrapper(referenceListAttribute,""));
        Assert.assertEquals("XXX",((ExampleFactoryA)((ReferenceListAttribute)copy.createAttribute()).get(0)).stringAttribute.get());
    }


}
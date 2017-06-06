package de.factoryfx.data.attribute;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import com.google.common.reflect.ClassPath;
import de.factoryfx.data.attribute.types.EnumAttribute;
import de.factoryfx.data.attribute.types.WrappingValueAttribute;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.merge.testfactories.ExampleFactoryA;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class AttributeJsonWrapperTest {
    private enum ExampleEnum{
        EXAMPLE
    }

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Collection<Object[]> data() throws IOException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        final ArrayList<Object[]> result = new ArrayList<>();
        for (ClassPath.ClassInfo classInfo : ClassPath.from(Attribute.class.getClassLoader()).getAllClasses()) {
            if (classInfo.getName().startsWith(Attribute.class.getPackage().getName())) {
                Class<?> clazz = classInfo.load();
                if (Attribute.class.isAssignableFrom(clazz) && !Modifier.isAbstract(clazz.getModifiers())) {
                    if (clazz==ReferenceAttribute.class){
                        result.add(new Attribute[]{new ReferenceAttribute(new AttributeMetadata(),Object.class)});
                        continue;
                    }
                    if (clazz==ReferenceListAttribute.class){
                        ReferenceListAttribute<ExampleFactoryA> referenceListAttribute= new ReferenceListAttribute<>(new AttributeMetadata(),ExampleFactoryA.class);
                        referenceListAttribute.add(new ExampleFactoryA());
                        result.add(new Attribute[]{referenceListAttribute});

                        continue;
                    }
                    if (clazz==EnumAttribute.class){
                        result.add(new Attribute[]{new EnumAttribute<>(ExampleEnum.class,new AttributeMetadata())});
                        continue;
                    }
                    if (clazz==ValueListAttribute.class){
                        result.add(new Attribute[]{new ValueListAttribute<>(String.class,new AttributeMetadata())});
                        continue;
                    }
                    if (clazz==ValueMapAttribute.class){
                        result.add(new Attribute[]{new ValueMapAttribute<>(new AttributeMetadata(),String.class,String.class)});
                        continue;
                    }
                    if (clazz==ValueSetAttribute.class){
                        final ValueSetAttribute<String> valueSetAttribute = new ValueSetAttribute<>(String.class, new AttributeMetadata());
                        final HashSet<String> hashSet = new HashSet<>();
                        hashSet.add("dfssfd");
                        valueSetAttribute.set(hashSet);
                        result.add(new Attribute[]{valueSetAttribute});

                        continue;
                    }

                    if (clazz==WrappingValueAttribute.class){
                        //not supported
                        continue;
                    }
                    if (clazz==ImmutableValueAttribute.class){
                        //not supported
                        continue;
                    }
                    if (clazz==ViewListReferenceAttribute.class){
                        //not supported
                        continue;
                    }
                    if (clazz==ViewReferenceAttribute.class){
                        //not supported
                        continue;
                    }
                    result.add(new Attribute[]{(Attribute) clazz.getConstructor(AttributeMetadata.class).newInstance(new AttributeMetadata())});
                }
            }
        }


        return result;
    }

    private Attribute attribute;

    public AttributeJsonWrapperTest(Attribute attribute) {
        this.attribute= attribute;
    }

    @Test
    public void test_createAttribute() {
        final Attribute attribute = new AttributeJsonWrapper(this.attribute, "").createAttribute();
    }

    @Test
    public void test_copy() {
        attribute.internal_copy();
    }

    @Test
    public void test_json() {
        final AttributeJsonWrapper copy = ObjectMapperBuilder.build().copy(new AttributeJsonWrapper(this.attribute, ""));
        copy.createAttribute().get();
        System.out.println( ObjectMapperBuilder.build().writeValueAsString(new AttributeJsonWrapper(this.attribute, "")));
    }

}
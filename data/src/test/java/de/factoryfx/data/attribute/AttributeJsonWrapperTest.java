package de.factoryfx.data.attribute;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;

import com.google.common.reflect.ClassPath;
import de.factoryfx.data.attribute.types.EnumAttribute;
import de.factoryfx.data.attribute.types.WrappingValueAttribute;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class AttributeJsonWrapperTest {

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
                        result.add(new Attribute[]{new ReferenceListAttribute(new AttributeMetadata(),Object.class)});
                        continue;
                    }
                    if (clazz==EnumAttribute.class){
                        result.add(new Attribute[]{new EnumAttribute(Enum.class,new AttributeMetadata())});
                        continue;
                    }
                    if (clazz==ValueListAttribute.class){
                        result.add(new Attribute[]{new ValueListAttribute(String.class,new AttributeMetadata())});
                        continue;
                    }
                    if (clazz==ValueMapAttribute.class){
                        result.add(new Attribute[]{new ValueMapAttribute(new AttributeMetadata(),String.class,String.class)});
                        continue;
                    }
                    if (clazz==ValueSetAttribute.class){
                        result.add(new Attribute[]{new ValueSetAttribute(String.class,new AttributeMetadata())});
                        continue;
                    }

                    if (clazz==WrappingValueAttribute.class){
                        //not supported
                        continue;
                    }
                    if (clazz==ValueAttribute.class){
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

//    @Test
//    public void test_copy() {
//        attribute.internal_copy();
//    }

}
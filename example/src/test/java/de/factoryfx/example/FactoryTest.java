package de.factoryfx.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.common.reflect.ClassPath;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.validator.FactoryValidator;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class FactoryTest {

    @Parameterized.Parameters(name = "{index}:{0}")
    public static Iterable<Object[]> data1() throws IOException {
        List<Object[]> result = new ArrayList<>();
        for (ClassPath.ClassInfo classInfo: ClassPath.from(FactoryTest.class.getClassLoader()).getAllClasses()){
            Class<?> load = classInfo.load();
            if (FactoryBase.class.isAssignableFrom(load) && load!=FactoryBase.class){
                result.add(new Object[]{load});
            }
        }

        return result;
    }

    Class<? extends FactoryBase> clazz;
    public FactoryTest(Class<? extends FactoryBase> clazz){
        this.clazz=clazz;
    }

    @Test
    public void test() throws IllegalAccessException, InstantiationException {
        Assert.assertEquals("",new FactoryValidator().validateFactory(clazz.newInstance()).orElse(""));
    }
}

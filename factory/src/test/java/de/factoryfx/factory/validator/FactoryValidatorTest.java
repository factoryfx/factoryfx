package de.factoryfx.factory.validator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import de.factoryfx.factory.util.ClasspathBasedFactoryProvider;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class FactoryValidatorTest {

    @Parameterized.Parameters(name = "{index}:{0}")
    public static Iterable<Object[]> data1() throws IOException {
        List<Object[]> result = new ArrayList<>();
        for (Class<? extends FactoryBase> clazz: new ClasspathBasedFactoryProvider().get(ExampleFactoryA.class)){
            result.add(new Object[]{clazz});
        }

        return result;
    }

    Class<? extends FactoryBase> clazz;
    public FactoryValidatorTest(Class<? extends FactoryBase> clazz){
        this.clazz=clazz;
    }

    @Test
    public void test() throws IllegalAccessException, InstantiationException {
        Assert.assertEquals("",new FactoryStyleValidator().validateFactory(clazz.newInstance()).orElse(""));
    }
}

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

    @Parameterized.Parameters(name = "{index}:{1}")
    public static Iterable<Object[]> data1() throws IOException {
        List<Object[]> result = new ArrayList<>();
        final FactoryStyleValidator factoryStyleValidator = new FactoryStyleValidator();
        for (Class<? extends FactoryBase> clazz: new ClasspathBasedFactoryProvider().get(ExampleFactoryA.class)){

            try {
                final List<FactoryStyleValidation> factoryValidations = factoryStyleValidator.createFactoryValidations(clazz.newInstance());
                for (FactoryStyleValidation factoryStyleValidation: factoryValidations){
                    result.add(new Object[]{factoryStyleValidation,clazz.getName()+":"+factoryStyleValidation.getClass().getSimpleName()});
                }
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
//            result.add(new Object[]{clazz});
        }

        return result;
    }

    FactoryStyleValidation factoryStyleValidation;
    public FactoryValidatorTest(FactoryStyleValidation factoryStyleValidation, String testname){
        this.factoryStyleValidation=factoryStyleValidation;
    }

    @Test
    public void test() throws IllegalAccessException, InstantiationException {
        Assert.assertEquals("",factoryStyleValidation.validateFactory().orElse(""));
    }
}

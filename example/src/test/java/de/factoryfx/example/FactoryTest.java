package de.factoryfx.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.factoryfx.example.factory.ShopFactory;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.util.ClasspathBasedFactoryProvider;
import de.factoryfx.factory.validator.FactoryStyleValidation;
import de.factoryfx.factory.validator.FactoryStyleValidator;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class FactoryTest {
    @Parameterized.Parameters(name = "{index}:{1}")
    public static Iterable<Object[]> data1() throws IOException {
        List<Object[]> result = new ArrayList<>();
        final FactoryStyleValidator factoryStyleValidator = new FactoryStyleValidator();
        for (Class<? extends FactoryBase> clazz: new ClasspathBasedFactoryProvider().get(ShopFactory.class)){

            try {
                final List<FactoryStyleValidation> factoryValidations = factoryStyleValidator.createFactoryValidations(clazz.newInstance());
                for (FactoryStyleValidation factoryStyleValidation: factoryValidations){
                    result.add(new Object[]{factoryStyleValidation,clazz.getName()+":"+factoryStyleValidation.getClass().getSimpleName()});
                }
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
//            result.add(new Object[]{clazz});
        }

        return result;
    }

    FactoryStyleValidation factoryStyleValidation;
    public FactoryTest(FactoryStyleValidation factoryStyleValidation, String testname){
        this.factoryStyleValidation=factoryStyleValidation;
    }

    @Test
    public void test() throws IllegalAccessException, InstantiationException {
        Assert.assertEquals("",factoryStyleValidation.validateFactory().orElse(""));
    }
}

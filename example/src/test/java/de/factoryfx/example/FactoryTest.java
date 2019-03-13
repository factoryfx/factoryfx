package de.factoryfx.example;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import de.factoryfx.example.server.ServerRootFactory;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.util.ClasspathBasedFactoryProvider;
import de.factoryfx.factory.validator.FactoryStyleValidation;
import de.factoryfx.factory.validator.FactoryStyleValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

public class FactoryTest {

    @TestFactory
    List<DynamicTest> createDynamicTests() {
        List<DynamicTest> result = new ArrayList<>();
        final FactoryStyleValidator factoryStyleValidator = new FactoryStyleValidator();
        for (Class<? extends FactoryBase> clazz: new ClasspathBasedFactoryProvider().get(ServerRootFactory.class)){
            if (!Modifier.isAbstract( clazz.getModifiers() )){
                try {
                    final List<FactoryStyleValidation> factoryValidations = factoryStyleValidator.createFactoryValidations(clazz.getConstructor().newInstance());
                    for (FactoryStyleValidation factoryStyleValidation: factoryValidations){

                        result.add(DynamicTest.dynamicTest(clazz.getName()+":"+factoryStyleValidation.getClass().getSimpleName(),
                                () -> Assertions.assertEquals("",factoryStyleValidation.validateFactory().orElse(""))));
                    }
                } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }

//            result.add(new Object[]{clazz});
        }


        return result;
    }

}

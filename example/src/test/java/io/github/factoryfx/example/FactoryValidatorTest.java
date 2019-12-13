package io.github.factoryfx.example;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.stream.Collectors;

import io.github.factoryfx.example.server.OrderMonitoringResourceFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import io.github.factoryfx.factory.util.ClasspathBasedFactoryProvider;
import io.github.factoryfx.factory.validator.FactoryStyleValidation;
import io.github.factoryfx.factory.validator.FactoryStyleValidatorBuilder;

public class FactoryValidatorTest {

    @TestFactory
    List<DynamicTest> createDynamicTests() {
        Class<?> rootClazz = OrderMonitoringResourceFactory.class;

        final FactoryStyleValidatorBuilder factoryStyleValidatorBuilder = new FactoryStyleValidatorBuilder();
        return new ClasspathBasedFactoryProvider().get(rootClazz.getPackage().getName())
                                                  .stream()
                                                  .filter(clazz -> !Modifier.isAbstract(clazz.getModifiers()))
                                                  .flatMap(clazz -> factoryStyleValidatorBuilder.createFactoryValidations(clazz)
                                                                                                .stream()
                                                                                                .map(factoryStyleValidation -> createTest(clazz, factoryStyleValidation)))
                                                  .collect(Collectors.toList());
    }
    private DynamicTest createTest(Class<?> clazz, FactoryStyleValidation factoryStyleValidation) {
        return DynamicTest.dynamicTest(clazz.getName() + ": " + factoryStyleValidation.getClass().getSimpleName(),
                                       () -> Assertions.assertNull(factoryStyleValidation.validateFactory().orElse(null)));
    }
}

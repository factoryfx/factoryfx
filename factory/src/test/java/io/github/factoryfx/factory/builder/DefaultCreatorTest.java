package io.github.factoryfx.factory.builder;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryBaseAttribute;
import io.github.factoryfx.factory.testfactories.ExampleLiveObjectA;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class DefaultCreatorTest {


    public static class ExampleFactoryANotNullable extends SimpleFactoryBase<ExampleLiveObjectA, ExampleFactoryANotNullable> {
        public final FactoryAttribute<ExampleLiveObjectA,ExampleFactoryANotNullable> referenceAttribute = new FactoryAttribute<>();

        @Override
        protected ExampleLiveObjectA createImpl() {
            return new ExampleLiveObjectA(null, null);
        }


    }


    @Test
    public void test_missing_factory(){
        Assertions.assertThrows(IllegalStateException.class, () -> {
            DefaultCreator<ExampleFactoryANotNullable, ExampleFactoryANotNullable> defaultCreator = new DefaultCreator<>(ExampleFactoryANotNullable.class);
            defaultCreator.apply(new FactoryContext<>());
        });
    }

    public static class ExampleFactoryNullableRef extends SimpleFactoryBase<ExampleLiveObjectA, ExampleFactoryNullableRef> {
        public final FactoryAttribute<ExampleLiveObjectA, ExampleFactoryNullableRef> referenceAttribute = new FactoryAttribute<ExampleLiveObjectA, ExampleFactoryNullableRef>().nullable();

        @Override
        protected ExampleLiveObjectA createImpl() {
            return new ExampleLiveObjectA(null, null);
        }


    }

    @Test
    public void test_missing_factory_nullable(){
        DefaultCreator<ExampleFactoryNullableRef,ExampleFactoryNullableRef> defaultCreator = new DefaultCreator<>(ExampleFactoryNullableRef.class);
        defaultCreator.apply(new FactoryContext<>());
    }


}
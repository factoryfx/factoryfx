package io.github.factoryfx.factory.builder;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryListAttribute;
import io.github.factoryfx.factory.testfactories.ExampleLiveObjectA;

public class DefaultCreatorTest {

    public static class ExampleFactoryANotNullable extends SimpleFactoryBase<ExampleLiveObjectA, ExampleFactoryANotNullable> {
        public final FactoryAttribute<ExampleLiveObjectA, ExampleFactoryANotNullable> referenceAttribute = new FactoryAttribute<>();

        @Override
        protected ExampleLiveObjectA createImpl() {
            return new ExampleLiveObjectA(null, null);
        }

    }

    @Test
    public void test_missing_factory() {
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
    public void test_missing_factory_nullable() {
        DefaultCreator<ExampleFactoryNullableRef, ExampleFactoryNullableRef> defaultCreator = new DefaultCreator<>(ExampleFactoryNullableRef.class);
        defaultCreator.apply(new FactoryContext<>());
    }

    public static class ExampleFactoryC extends SimpleFactoryBase<ExampleLiveObjectA, RootFactory> {

        @Override
        protected ExampleLiveObjectA createImpl() {
            return new ExampleLiveObjectA(null, null);
        }
    }

    public static class RootFactory extends SimpleFactoryBase<Void, RootFactory> {

        public final FactoryListAttribute<ExampleLiveObjectA, ExampleFactoryC> exampleFactoryC = new FactoryListAttribute<>();

        @Override
        protected Void createImpl() {
            return null;
        }
    }

    @Test
    void test_RefList() {
        FactoryTreeBuilder<Void, RootFactory> builder = new FactoryTreeBuilder<>(RootFactory.class);
        builder.addSingleton(ExampleFactoryC.class);
        RootFactory rootFactory = builder.buildTree();
        assertEquals(0, rootFactory.exampleFactoryC.size());
    }

}
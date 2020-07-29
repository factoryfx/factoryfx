package io.github.factoryfx.factory.attribute.primitive;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.merge.DataMerger;
import io.github.factoryfx.factory.merge.MergeDiffInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.factory.attribute.primitive.BooleanAttribute;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.builder.MicroserviceBuilder;
import io.github.factoryfx.factory.log.FactoryUpdateLog;
import io.github.factoryfx.server.Microservice;

public class BooleanAttributeSetNullTest {

    public static class RootFactory extends FactoryBase<String, RootFactory> {
        public final FactoryAttribute<String, ChildFactory> childFactoryFactory = new FactoryAttribute<String, ChildFactory>().nullable();
        public RootFactory() {
            configLifeCycle().setCreator(() -> "abc");
        }
    }

    public static class ChildFactory extends SimpleFactoryBase<String, RootFactory> {
        public final BooleanAttribute booleanAttribute = new BooleanAttribute().defaultValue(false);

        @Override
        protected String createImpl() {
            return "aaa";
        }
    }

    @Test
    void testSetAttributeToNull() {
        FactoryTreeBuilder<String, RootFactory> factoryTreeBuilder = new FactoryTreeBuilder<>(RootFactory.class, rootFactoryFactoryContext -> {
            RootFactory factory = new RootFactory();
            return factory;
        });

        MicroserviceBuilder<String, RootFactory> microservice = factoryTreeBuilder.microservice();
        Microservice<String, RootFactory> build = microservice.build();
        build.start();

        { // update 1, attribute set to null (has a default value)
            var upd = build.prepareNewFactory();
            ChildFactory childFactory = new ChildFactory();
            childFactory.booleanAttribute.set(null);  // <- removing this line will make it work as expected
            upd.root.childFactoryFactory.set(childFactory);

            FactoryUpdateLog<RootFactory> rootFactoryFactoryUpdateLog = build.updateCurrentFactory(upd);
            rootFactoryFactoryUpdateLog.dumpError(System.out::println);
            Assertions.assertTrue(rootFactoryFactoryUpdateLog.successfullyMerged());
            Assertions.assertFalse(rootFactoryFactoryUpdateLog.failedUpdate());
        }

        { // update 2 removes the 'faulty' factory added in previous update
            var upd = build.prepareNewFactory();
            upd.root.childFactoryFactory.set(null);
            FactoryUpdateLog<RootFactory> rootFactoryFactoryUpdateLog = build.updateCurrentFactory(upd);
            rootFactoryFactoryUpdateLog.dumpError(System.out::println);
            Assertions.assertTrue(rootFactoryFactoryUpdateLog.successfullyMerged());
            Assertions.assertFalse(rootFactoryFactoryUpdateLog.failedUpdate());
        }

    }

    @Test
    void merge_test(){
        ChildFactory childFactory = new ChildFactory();
        childFactory.booleanAttribute.set(null);
        RootFactory root = new RootFactory();
        root.childFactoryFactory.set(childFactory);

        RootFactory update = root.internal().copy();
        update.childFactoryFactory.set(null);

        DataMerger<RootFactory> merger = new DataMerger<>(root, root.internal().copy(), update);
        MergeDiffInfo<RootFactory> mergeDiffInfo = merger.mergeIntoCurrent((p) -> true);
        Assertions.assertTrue(mergeDiffInfo.hasNoConflicts());
    }

    @Test
    void json_test(){
        ChildFactory childFactory = new ChildFactory();
        childFactory.booleanAttribute.set(null);

        System.out.println(ObjectMapperBuilder.build().writeValueAsString(childFactory));

        ChildFactory copy = ObjectMapperBuilder.build().copy(childFactory);
        Assertions.assertNull(copy.booleanAttribute.get());
    }

    @Test
    void json_nullable(){
        ChildFactory childFactory = new ChildFactory();
        childFactory.booleanAttribute.set(null);
        Assertions.assertNull(childFactory.booleanAttribute.get());
    }

    @Test
    void merge_copy(){
        ChildFactory childFactory = new ChildFactory();
        childFactory.booleanAttribute.set(null);
        RootFactory root = new RootFactory();
        root.childFactoryFactory.set(childFactory);

        RootFactory copy = root.internal().copy();
        Assertions.assertNull(copy.childFactoryFactory.get().booleanAttribute.get());
    }

    @Test
    public void test_json_compatibility(){
        String old =
                "{\n" +
                "  \"@class\" : \"io.github.factoryfx.factory.BooleanAttributeSetNullTest$ChildFactory\",\n" +
                "  \"id\" : \"c2e46d42-a11e-8c61-65cb-43fb9a5e58e9\",\n" +
                "  \"treeBuilderClassUsed\" : false,\n" +
                "  \"booleanAttribute\" : { }\n" +
                "}\n";
        ObjectMapperBuilder.build().readValue(old,ChildFactory.class);
        //no exception
    }

}

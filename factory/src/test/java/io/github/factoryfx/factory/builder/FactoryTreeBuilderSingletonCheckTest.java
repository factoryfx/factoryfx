package io.github.factoryfx.factory.builder;

import io.github.factoryfx.factory.testfactories.ExampleFactoryA;
import io.github.factoryfx.factory.testfactories.ExampleFactoryB;
import io.github.factoryfx.factory.testfactories.ExampleLiveObjectA;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class FactoryTreeBuilderSingletonCheckTest {

    @Test
    public void duplicate_unnamed_singleton_is_reported_with_paths() {
        FactoryTreeBuilder<ExampleLiveObjectA, ExampleFactoryA> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class, context -> {
            ExampleFactoryA root = new ExampleFactoryA();
            root.referenceAttribute.set(context.get(ExampleFactoryB.class));
            root.referenceListAttribute.add(new ExampleFactoryB());//manually created duplicate of the singleton
            return root;
        });
        builder.addSingleton(ExampleFactoryB.class, context -> new ExampleFactoryB());

        ExampleFactoryA root = builder.buildTreeUnvalidated();
        List<String> warnings = builder.checkSingletonUsage(root);

        Assertions.assertEquals(1, warnings.size());
        Assertions.assertTrue(warnings.get(0).contains("2 times"), warnings.get(0));
        Assertions.assertTrue(warnings.get(0).contains(ExampleFactoryB.class.getName()), warnings.get(0));
        Assertions.assertTrue(warnings.get(0).contains("ExampleFactoryA/ExampleFactoryB"), warnings.get(0));
    }

    @Test
    public void multiple_references_to_the_same_singleton_instance_are_fine() {
        FactoryTreeBuilder<ExampleLiveObjectA, ExampleFactoryA> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class, context -> {
            ExampleFactoryA root = new ExampleFactoryA();
            root.referenceAttribute.set(context.get(ExampleFactoryB.class));
            root.referenceListAttribute.add(context.get(ExampleFactoryB.class));//same instance again
            return root;
        });
        builder.addSingleton(ExampleFactoryB.class, context -> new ExampleFactoryB());

        ExampleFactoryA root = builder.buildTreeUnvalidated();

        Assertions.assertTrue(builder.checkSingletonUsage(root).isEmpty());
    }

    @Test
    public void named_singletons_of_the_same_class_are_fine() {
        FactoryTreeBuilder<ExampleLiveObjectA, ExampleFactoryA> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class, context -> {
            ExampleFactoryA root = new ExampleFactoryA();
            root.referenceAttribute.set(context.get(ExampleFactoryB.class, "b1"));
            root.referenceListAttribute.add(context.get(ExampleFactoryB.class, "b2"));
            return root;
        });
        builder.addSingleton(ExampleFactoryB.class, "b1", context -> new ExampleFactoryB());
        builder.addSingleton(ExampleFactoryB.class, "b2", context -> new ExampleFactoryB());

        ExampleFactoryA root = builder.buildTreeUnvalidated();

        Assertions.assertTrue(builder.checkSingletonUsage(root).isEmpty());
    }

    @Test
    public void duplicated_named_singleton_is_reported_with_name() {
        FactoryTreeBuilder<ExampleLiveObjectA, ExampleFactoryA> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class, context -> {
            ExampleFactoryA root = new ExampleFactoryA();
            root.referenceAttribute.set(context.get(ExampleFactoryB.class, "b1"));
            ExampleFactoryB duplicate = new ExampleFactoryB();
            duplicate.internal().setTreeBuilderName("b1");//same effect as duplicating the factory with copy
            root.referenceListAttribute.add(duplicate);
            return root;
        });
        builder.addSingleton(ExampleFactoryB.class, "b1", context -> new ExampleFactoryB());

        ExampleFactoryA root = builder.buildTreeUnvalidated();
        List<String> warnings = builder.checkSingletonUsage(root);

        Assertions.assertEquals(1, warnings.size());
        Assertions.assertTrue(warnings.get(0).contains("name='b1'"), warnings.get(0));
    }

    @Test
    public void prototypes_may_occur_multiple_times() {
        FactoryTreeBuilder<ExampleLiveObjectA, ExampleFactoryA> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class, context -> {
            ExampleFactoryA root = new ExampleFactoryA();
            root.referenceAttribute.set(context.get(ExampleFactoryB.class));
            root.referenceListAttribute.add(context.get(ExampleFactoryB.class));//prototype: new instance each get
            return root;
        });
        builder.addPrototype(ExampleFactoryB.class, context -> new ExampleFactoryB());

        ExampleFactoryA root = builder.buildTreeUnvalidated();

        Assertions.assertTrue(builder.checkSingletonUsage(root).isEmpty());
    }
}

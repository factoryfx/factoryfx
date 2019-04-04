package io.github.factoryfx.factory.builder;

import io.github.factoryfx.factory.BranchSelector;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.storage.migration.metadata.DataStorageMetadataDictionary;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.PolymorphicFactoryBase;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryPolymorphicReferenceAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryReferenceAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryReferenceListAttribute;
import io.github.factoryfx.factory.testfactories.*;
import io.github.factoryfx.factory.testfactories.poly.ErrorPrinter;
import io.github.factoryfx.factory.testfactories.poly.OutPrinterFactory;
import io.github.factoryfx.factory.testfactories.poly.Printer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FactoryTreeBuilderTest {

    @Test
    public void test_simple(){
        FactoryTreeBuilder<ExampleLiveObjectA,ExampleFactoryA,Void> factoryTreeBuilder = new FactoryTreeBuilder<>(ExampleFactoryA.class, context -> {
            ExampleFactoryA factory = new ExampleFactoryA();
            factory.referenceListAttribute.set(context.getList(io.github.factoryfx.factory.testfactories.ExampleFactoryB.class));
            factory.referenceAttribute.set(context.get(io.github.factoryfx.factory.testfactories.ExampleFactoryB.class));
            return factory;
        });
        factoryTreeBuilder.addFactory(io.github.factoryfx.factory.testfactories.ExampleFactoryB.class, Scope.PROTOTYPE, context -> {
            io.github.factoryfx.factory.testfactories.ExampleFactoryB factory = new io.github.factoryfx.factory.testfactories.ExampleFactoryB();
            factory.referenceAttributeC.set(context.get(io.github.factoryfx.factory.testfactories.ExampleFactoryC.class));
//            factory.referenceAttribute.set(context.get(ExampleFactoryA.class));
            return factory;
        });
        factoryTreeBuilder.addFactory(io.github.factoryfx.factory.testfactories.ExampleFactoryC.class, Scope.PROTOTYPE, context -> {
            io.github.factoryfx.factory.testfactories.ExampleFactoryC factory = new io.github.factoryfx.factory.testfactories.ExampleFactoryC();
//            factory.referenceAttribute.set(context.get(ExampleFactoryB.class));
            return factory;
        });


        ExampleFactoryA root = factoryTreeBuilder.buildTreeUnvalidated();


        System.out.println(ObjectMapperBuilder.build().writeValueAsString(root));
    }

    @Test
    public void test_simple_defaultcreator(){
        FactoryTreeBuilder<ExampleLiveObjectA,ExampleFactoryA,Void> factoryTreeBuilder = new FactoryTreeBuilder<>(ExampleFactoryA.class);
        factoryTreeBuilder.addFactory(io.github.factoryfx.factory.testfactories.ExampleFactoryB.class, Scope.PROTOTYPE, context -> {
            io.github.factoryfx.factory.testfactories.ExampleFactoryB factory = new io.github.factoryfx.factory.testfactories.ExampleFactoryB();
            factory.referenceAttributeC.set(context.get(io.github.factoryfx.factory.testfactories.ExampleFactoryC.class));
//            factory.referenceAttribute.set(context.get(ExampleFactoryA.class));
            return factory;
        });
        factoryTreeBuilder.addFactory(io.github.factoryfx.factory.testfactories.ExampleFactoryC.class, Scope.PROTOTYPE, context -> {
            io.github.factoryfx.factory.testfactories.ExampleFactoryC factory = new io.github.factoryfx.factory.testfactories.ExampleFactoryC();
//            factory.referenceAttribute.set(context.get(ExampleFactoryB.class));
            return factory;
        });

        ExampleFactoryA root = factoryTreeBuilder.buildTreeUnvalidated();

        System.out.println(ObjectMapperBuilder.build().writeValueAsString(root));
    }

    public static class TreeFactoryTestA extends SimpleFactoryBase<Void, TreeFactoryTestA> {

        public final FactoryReferenceAttribute<TreeFactoryTestA,ExampleLiveObjectB, TreeExampleFactoryB> referenceAttribute1 = new FactoryReferenceAttribute<TreeFactoryTestA,ExampleLiveObjectB, TreeExampleFactoryB>().labelText("ExampleA2");
        public final FactoryReferenceAttribute<TreeFactoryTestA,ExampleLiveObjectB, TreeExampleFactoryB> referenceAttribute2 = new FactoryReferenceAttribute<TreeFactoryTestA,ExampleLiveObjectB, TreeExampleFactoryB>().labelText("ExampleA2");
        public final FactoryReferenceListAttribute<TreeFactoryTestA,ExampleLiveObjectB, TreeExampleFactoryB> referenceList = new FactoryReferenceListAttribute<TreeFactoryTestA,ExampleLiveObjectB, TreeExampleFactoryB>().labelText("ExampleA3");


        @Override
        public Void createImpl() {
            return null;
        }
    }

    public static class TreeExampleFactoryC extends SimpleFactoryBase<ExampleLiveObjectC, TreeFactoryTestA> {
        public final StringAttribute stringAttribute= new StringAttribute().labelText("ExampleB1");
        public final FactoryReferenceAttribute<TreeFactoryTestA,ExampleLiveObjectB, TreeExampleFactoryB> referenceAttribute = new FactoryReferenceAttribute<>();

        @Override
        public ExampleLiveObjectC createImpl() {
            return new ExampleLiveObjectC();
        }

    }

    public static class TreeExampleFactoryB extends SimpleFactoryBase<ExampleLiveObjectB, TreeFactoryTestA> {
        public final StringAttribute stringAttribute= new StringAttribute().labelText("ExampleB1");
        public final FactoryReferenceAttribute<TreeFactoryTestA,Void, TreeFactoryTestA> referenceAttribute = new FactoryReferenceAttribute<>();
        public final FactoryReferenceAttribute<TreeFactoryTestA,ExampleLiveObjectC, TreeExampleFactoryC> referenceAttributeC = new FactoryReferenceAttribute<>();

        @Override
        public ExampleLiveObjectB createImpl() {
            return new ExampleLiveObjectB(referenceAttributeC.instance());
        }

    }


    @Test
    public void test_singelton(){
        FactoryTreeBuilder<Void, TreeFactoryTestA,Void> factoryTreeBuilder = new FactoryTreeBuilder<>(TreeFactoryTestA.class, context -> {
            TreeFactoryTestA factory = new TreeFactoryTestA();
            factory.referenceAttribute1.set(context.get(TreeExampleFactoryB.class));
            factory.referenceAttribute2.set(context.get(TreeExampleFactoryB.class));
            return factory;
        });
        factoryTreeBuilder.addFactory(TreeExampleFactoryB.class, Scope.SINGLETON, context -> {
            TreeExampleFactoryB factory = new TreeExampleFactoryB();
//            factory.referenceAttributeC.set(context.get(FactoryTestA.class));
            return factory;
        });

        TreeFactoryTestA root = factoryTreeBuilder.buildTreeUnvalidated();
        assertEquals(root.referenceAttribute1.get(),root.referenceAttribute2.get());
    }

    @Test
    public void test_prototype(){
        FactoryTreeBuilder<Void, TreeFactoryTestA,Void> factoryTreeBuilder = new FactoryTreeBuilder<>(TreeFactoryTestA.class, context -> {
            TreeFactoryTestA factory = new TreeFactoryTestA();
            factory.referenceAttribute1.set(context.get(TreeExampleFactoryB.class));
            factory.referenceAttribute2.set(context.get(TreeExampleFactoryB.class));
            return factory;
        });
        factoryTreeBuilder.addFactory(TreeExampleFactoryB.class, Scope.PROTOTYPE, context -> {
            TreeExampleFactoryB factory = new TreeExampleFactoryB();
            return factory;
        });

        TreeFactoryTestA root = factoryTreeBuilder.buildTreeUnvalidated();
        Assertions.assertNotEquals(root.referenceAttribute1.get(),root.referenceAttribute2.get());

    }

    private static class ErrorPrinterFactory2 extends PolymorphicFactoryBase<Printer,ExamplePolymorphic> {
        @Override
        public Printer createImpl() {
            return new ErrorPrinter();
        }

    }

    public static class ExamplePolymorphic extends FactoryBase<Void,ExamplePolymorphic> {
        public final FactoryPolymorphicReferenceAttribute<ExamplePolymorphic,Printer> attribute = new FactoryPolymorphicReferenceAttribute<>(Printer.class, ErrorPrinterFactory2.class, OutPrinterFactory.class);
    }


    @Test
    public void test_polymorphic(){
        FactoryTreeBuilder<Void,ExamplePolymorphic,Void> factoryTreeBuilder = new FactoryTreeBuilder<>(ExamplePolymorphic.class);

        factoryTreeBuilder.addFactory(ErrorPrinterFactory2.class, Scope.SINGLETON);

        ExamplePolymorphic root = factoryTreeBuilder.buildTreeUnvalidated();
        assertNotNull(root.attribute.get());

    }


    @Test
    public void test_simple_validation(){
        Assertions.assertThrows(IllegalStateException.class, () -> {
            FactoryTreeBuilder< Void, TreeFactoryTestA, Void> factoryTreeBuilder = new FactoryTreeBuilder<>(TreeFactoryTestA.class);
            factoryTreeBuilder.addFactory(TreeExampleFactoryB.class, Scope.PROTOTYPE, context -> {
                TreeExampleFactoryB factory = new TreeExampleFactoryB();
                factory.referenceAttributeC.set(context.get(TreeExampleFactoryC.class));
                factory.referenceAttribute.set(new TreeFactoryTestA());
                return factory;
            });
            factoryTreeBuilder.addFactory(TreeExampleFactoryC.class, Scope.PROTOTYPE, context -> {
                TreeExampleFactoryC factory = new TreeExampleFactoryC();
                factory.referenceAttribute.set(new TreeExampleFactoryB());
                return factory;
            });

            TreeFactoryTestA root = factoryTreeBuilder.buildTree();

            System.out.println(ObjectMapperBuilder.build().writeValueAsString(root));
        });
    }

    @Test
    public void test_root_creator_missing(){
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            FactoryTreeBuilder< Void, TreeFactoryTestA, Void> factoryTreeBuilder = new FactoryTreeBuilder<>(null);

            factoryTreeBuilder.addFactory(TreeExampleFactoryB.class, Scope.PROTOTYPE, context -> {
                TreeExampleFactoryB factory = new TreeExampleFactoryB();
                factory.referenceAttributeC.set(context.get(TreeExampleFactoryC.class));
                factory.referenceAttribute.set(new TreeFactoryTestA());
                return factory;
            });
            factoryTreeBuilder.addFactory(TreeExampleFactoryC.class, Scope.PROTOTYPE, context -> {
                TreeExampleFactoryC factory = new TreeExampleFactoryC();
                factory.referenceAttribute.set(new TreeExampleFactoryB());
                return factory;
            });

            factoryTreeBuilder.buildTreeUnvalidated();
        });
    }

    @Test
    public void test_root_mandatory(){
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new FactoryTreeBuilder<>(null);
        });
    }

    @Test
    public void test_singleton_named(){
        FactoryTreeBuilder<Void, TreeFactoryTestA,Void> factoryTreeBuilder = new FactoryTreeBuilder<>(TreeFactoryTestA.class, context -> {
            TreeFactoryTestA factory = new TreeFactoryTestA();
            factory.referenceList.add(context.get(TreeExampleFactoryB.class,"111"));
            factory.referenceList.add(context.get(TreeExampleFactoryB.class,"222"));
            return factory;
        });
        factoryTreeBuilder.addFactory(TreeExampleFactoryB.class,"111", Scope.SINGLETON, context -> {
            TreeExampleFactoryB factory = new TreeExampleFactoryB();
            factory.referenceAttributeC.set(null);
            factory.referenceAttribute.set(new TreeFactoryTestA());
            return factory;
        });
        factoryTreeBuilder.addFactory(TreeExampleFactoryB.class,"222", Scope.SINGLETON, context -> {
            TreeExampleFactoryB factory = new TreeExampleFactoryB();
            factory.referenceAttributeC.set(null);
            factory.referenceAttribute.set(new TreeFactoryTestA());
            return factory;
        });
        TreeFactoryTestA factoryTestA = factoryTreeBuilder.buildTreeUnvalidated();

        Assertions.assertTrue(factoryTestA.referenceList.get(0)!=factoryTestA.referenceList.get(1));
    }

    @Test
    public void test_singleton_named_getList(){
        FactoryTreeBuilder<Void, TreeFactoryTestA,Void> factoryTreeBuilder = new FactoryTreeBuilder<>(TreeFactoryTestA.class, context -> {
            TreeFactoryTestA factory = new TreeFactoryTestA();
            factory.referenceList.addAll(context.getList(TreeExampleFactoryB.class));
            return factory;
        });
        factoryTreeBuilder.addFactory(TreeExampleFactoryB.class,"111", Scope.SINGLETON, context -> {
            TreeExampleFactoryB factory = new TreeExampleFactoryB();
            factory.referenceAttributeC.set(null);
            factory.referenceAttribute.set(new TreeFactoryTestA());
            return factory;
        });
        factoryTreeBuilder.addFactory(TreeExampleFactoryB.class,"222", Scope.SINGLETON, context -> {
            TreeExampleFactoryB factory = new TreeExampleFactoryB();
            factory.referenceAttributeC.set(null);
            factory.referenceAttribute.set(new TreeFactoryTestA());
            return factory;
        });
        TreeFactoryTestA factoryTestA = factoryTreeBuilder.buildTreeUnvalidated();

        Assertions.assertTrue(factoryTestA.referenceList.get(0)!=factoryTestA.referenceList.get(1));
    }

    @Test
    public void test_mutiple_named_getList(){
        FactoryTreeBuilder<Void, TreeFactoryTestA,Void> factoryTreeBuilder = createBuilder();
        TreeFactoryTestA factoryTestA = factoryTreeBuilder.buildTreeUnvalidated();

        Assertions.assertTrue(factoryTestA.referenceList.get(0)==factoryTestA.referenceList.get(2));
        Assertions.assertTrue(factoryTestA.referenceList.get(1)==factoryTestA.referenceList.get(3));

        FactoryTreeBuilder<Void, TreeFactoryTestA,Void> factoryTreeBuilder2 = createBuilder();
        factoryTreeBuilder2.fillFromExistingFactoryTree(factoryTestA);
        TreeFactoryTestA factoryTestA2 = factoryTreeBuilder.buildTreeUnvalidated();

        Assertions.assertTrue(factoryTestA2.referenceList.get(0)==factoryTestA2.referenceList.get(2));
        Assertions.assertTrue(factoryTestA2.referenceList.get(1)==factoryTestA2.referenceList.get(3));
    }

    private FactoryTreeBuilder<Void, TreeFactoryTestA,Void> createBuilder() {
        FactoryTreeBuilder<Void, TreeFactoryTestA,Void> factoryTreeBuilder = new FactoryTreeBuilder<>(TreeFactoryTestA.class, context -> {
            TreeFactoryTestA factory = new TreeFactoryTestA();
            factory.referenceList.addAll(context.getList(TreeExampleFactoryB.class));
            factory.referenceList.addAll(context.getList(TreeExampleFactoryB.class));
            return factory;
        });
        factoryTreeBuilder.addFactory(TreeExampleFactoryB.class,"111", Scope.SINGLETON, context -> {
            TreeExampleFactoryB factory = new TreeExampleFactoryB();
            return factory;
        });
        factoryTreeBuilder.addFactory(TreeExampleFactoryB.class,"222", Scope.SINGLETON, context -> {
            TreeExampleFactoryB factory = new TreeExampleFactoryB();
            return factory;
        });
        return factoryTreeBuilder;
    }


    @Test
    public void test_incomplete_builder_class() {
        Assertions.assertThrows(IllegalStateException.class, () -> {

            FactoryTreeBuilder< ExampleLiveObjectA, ExampleFactoryA, Void> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class, context -> {
                ExampleFactoryA factoryBases = new ExampleFactoryA();
                factoryBases.referenceAttribute.set(context.get(io.github.factoryfx.factory.testfactories.ExampleFactoryB.class));
                factoryBases.referenceAttribute.set(null);
                return factoryBases;
            });
            //intentional commented out
//        builder.addFactory(ExampleFactoryB.class, Scope.PROTOTYPE, context -> {
//            ExampleFactoryB factoryBases = new ExampleFactoryB();
//            factoryBases.stringAttribute.set("123");
//            factoryBases.referenceAttributeC.set(context.get(ExampleFactoryC.class));
//            return factoryBases;
//        });


            ExampleFactoryA root = builder.buildTreeUnvalidated();
        });
    }

    @Test
    public void test_incomplete_builder_classAndName() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            FactoryTreeBuilder< ExampleLiveObjectA, ExampleFactoryA, Void> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class, context -> {
                ExampleFactoryA factoryBases = new ExampleFactoryA();
                factoryBases.referenceAttribute.set(context.get(io.github.factoryfx.factory.testfactories.ExampleFactoryB.class, "dfgdgf"));
                factoryBases.referenceAttribute.set(null);
                return factoryBases;
            });
            //intentional commented out
//        builder.addFactory(ExampleFactoryB.class, Scope.PROTOTYPE, context -> {
//            ExampleFactoryB factoryBases = new ExampleFactoryB();
//            factoryBases.stringAttribute.set("123");
//            factoryBases.referenceAttributeC.set(context.get(ExampleFactoryC.class));
//            return factoryBases;
//        });


            ExampleFactoryA root = builder.buildTreeUnvalidated();
        });
    }

    @Test
    public void test_dataDictionary(){
        FactoryTreeBuilder<Void, TreeFactoryTestA,Void> builder = new FactoryTreeBuilder<>(TreeFactoryTestA.class, context -> {
            TreeFactoryTestA factory = new TreeFactoryTestA();
            factory.referenceAttribute1.set(context.get(TreeExampleFactoryB.class));
            return factory;
        });
        builder.addFactory(TreeExampleFactoryB.class, Scope.SINGLETON, context -> {
            return new TreeExampleFactoryB();
        });

        DataStorageMetadataDictionary dataStorageMetadataDictionary = builder.buildTreeUnvalidated().internal().createDataStorageMetadataDictionaryFromRoot();
        Assertions.assertTrue(dataStorageMetadataDictionary.containsClass(TreeFactoryTestA.class.getName()));
        Assertions.assertTrue(dataStorageMetadataDictionary.containsClass(TreeExampleFactoryB.class.getName()));
    }

    public static class ExampleFactoryB2 extends TreeExampleFactoryB {
    }

    @Test
    public void test_dataDictionary_inheritance(){
        FactoryTreeBuilder<Void, TreeFactoryTestA,Void> builder = new FactoryTreeBuilder<>(TreeFactoryTestA.class, context -> {
            TreeFactoryTestA factory = new TreeFactoryTestA();
            factory.referenceAttribute1.set(context.get(TreeExampleFactoryB.class));
            return factory;
        });
        builder.addFactory(TreeExampleFactoryB.class, Scope.SINGLETON, context -> {
            return new ExampleFactoryB2();
        });

        DataStorageMetadataDictionary dataStorageMetadataDictionary = builder.buildTreeUnvalidated().internal().createDataStorageMetadataDictionaryFromRoot();
        Assertions.assertTrue(dataStorageMetadataDictionary.containsClass(TreeFactoryTestA.class.getName()));
        Assertions.assertTrue(dataStorageMetadataDictionary.containsClass(ExampleFactoryB2.class.getName()));
    }


    @Test
    public void test_double_add_factory(){
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            FactoryTreeBuilder<Void, TreeFactoryTestA, Void> builder = new FactoryTreeBuilder<>(TreeFactoryTestA.class, context -> {
                TreeFactoryTestA factory = new TreeFactoryTestA();
                factory.referenceAttribute1.set(context.get(TreeExampleFactoryB.class));
                return factory;
            });
            builder.addFactory(TreeExampleFactoryB.class, Scope.SINGLETON, context -> {
                return new ExampleFactoryB2();
            });
            builder.addFactory(TreeExampleFactoryB.class, Scope.SINGLETON, context -> {
                return new ExampleFactoryB2();
            });

        });

    }


    @Test
    public void test_branch_selector(){
        FactoryTreeBuilder<ExampleLiveObjectA,ExampleFactoryA,Void> factoryTreeBuilder = new FactoryTreeBuilder<>(ExampleFactoryA.class, context -> {
            ExampleFactoryA factory = new ExampleFactoryA();
            factory.referenceListAttribute.set(context.getList(ExampleFactoryB.class));
            factory.referenceAttribute.set(context.get(ExampleFactoryB.class));
            return factory;
        });
        factoryTreeBuilder.addFactory(ExampleFactoryB.class, Scope.PROTOTYPE, context -> {
            io.github.factoryfx.factory.testfactories.ExampleFactoryB factory = new ExampleFactoryB();
            factory.referenceAttributeC.set(context.get(io.github.factoryfx.factory.testfactories.ExampleFactoryC.class));
            return factory;
        });
        factoryTreeBuilder.addFactory(ExampleFactoryC.class, Scope.PROTOTYPE, context -> {
            ExampleFactoryC factory = new ExampleFactoryC();
            factory.referenceAttribute.set(new ExampleFactoryB());
            factory.stringAttribute.set("hggj");
            return factory;
        });


        ExampleLiveObjectA liveBranch = factoryTreeBuilder.branch().select(ExampleFactoryA.class).instance();
        assertNotNull(liveBranch);

        Set<BranchSelector.Branch<ExampleFactoryA, ExampleLiveObjectB, ExampleFactoryB>> branches = factoryTreeBuilder.branch().selectPrototype(ExampleFactoryB.class);
        assertEquals(4,branches.size());
        for (BranchSelector.Branch<ExampleFactoryA, ExampleLiveObjectB, ExampleFactoryB> branch : branches) {
            assertNotNull(branch.instance());
        }
    }

    @Test
    public void test_branch_selector_as_buildSubTree(){
        FactoryTreeBuilder<ExampleLiveObjectA,ExampleFactoryA,Void> factoryTreeBuilder = new FactoryTreeBuilder<>(ExampleFactoryA.class, context -> {
            ExampleFactoryA factory = new ExampleFactoryA();
            factory.referenceListAttribute.set(context.getList(ExampleFactoryB.class));
            factory.referenceAttribute.set(context.get(ExampleFactoryB.class));
            return factory;
        });
        factoryTreeBuilder.addFactory(ExampleFactoryB.class, Scope.PROTOTYPE, context -> {
            io.github.factoryfx.factory.testfactories.ExampleFactoryB factory = new ExampleFactoryB();
            factory.referenceAttributeC.set(context.get(io.github.factoryfx.factory.testfactories.ExampleFactoryC.class));
            return factory;
        });
        factoryTreeBuilder.addFactory(ExampleFactoryC.class, Scope.PROTOTYPE, context -> {
            ExampleFactoryC factory = new ExampleFactoryC();
            factory.referenceAttribute.set(new ExampleFactoryB());
            factory.stringAttribute.set("hggj");
            return factory;
        });


        ExampleFactoryA liveBranch = factoryTreeBuilder.branch().select(ExampleFactoryA.class).factory();
        assertNotNull(liveBranch);
    }

    @Test
    public void tets_multiple_name(){
        FactoryTreeBuilder<ExampleLiveObjectA,ExampleFactoryA,Void> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class, ctx -> {
            ExampleFactoryA root = new ExampleFactoryA();
            root.referenceListAttribute.add(ctx.get(ExampleFactoryB.class,"1"));
            root.referenceListAttribute.add(ctx.get(ExampleFactoryB.class,"2"));
            root.referenceListAttribute.add(ctx.get(ExampleFactoryB.class));
            return root;
        });
        builder.addFactory(ExampleFactoryB.class, "1",Scope.SINGLETON, ctx -> new ExampleFactoryB());
        builder.addFactory(ExampleFactoryB.class, "2",Scope.SINGLETON, ctx -> new ExampleFactoryB());
        builder.addFactory(ExampleFactoryB.class, Scope.SINGLETON, ctx -> {
            ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
            exampleFactoryB.stringAttribute.set("bla");
            return exampleFactoryB;
        });
        ExampleFactoryA root = builder.buildTree();
        assertEquals(4, root.internal().collectChildrenDeep().size());
        assertEquals("1", root.referenceListAttribute.get(0).internal().getTreeBuilderName());
        assertEquals("2", root.referenceListAttribute.get(1).internal().getTreeBuilderName());
        assertEquals(null, root.referenceListAttribute.get(2).internal().getTreeBuilderName());
        assertEquals("bla", root.referenceListAttribute.get(2).stringAttribute.get());
    }

}
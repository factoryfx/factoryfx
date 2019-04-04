package io.github.factoryfx.factory;

import io.github.factoryfx.factory.builder.FactoryContext;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.builder.Scope;
import io.github.factoryfx.factory.testfactories.ExampleFactoryA;
import io.github.factoryfx.factory.testfactories.ExampleFactoryB;
import io.github.factoryfx.factory.testfactories.ExampleLiveObjectA;
import io.github.factoryfx.factory.testfactories.ExampleLiveObjectB;
import org.checkerframework.checker.units.qual.K;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.MockUtil;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class BranchSelectorTest {

    @Test
    public void test(){
        FactoryTreeBuilder<ExampleLiveObjectA,ExampleFactoryA,Void> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class, ctx -> {
            ExampleFactoryA root = new ExampleFactoryA();
            root.referenceAttribute.set(ctx.get(ExampleFactoryB.class));
            return root;
        });
        builder.addFactory(ExampleFactoryB.class, Scope.SINGLETON, ctx -> new ExampleFactoryB());


        BranchSelector<ExampleFactoryA> subTreeUtility = new BranchSelector<>(builder);
        assertNotNull(subTreeUtility.select(ExampleFactoryB.class).instance());
    }

    @Test
    public void test_start_stop(){
        FactoryTreeBuilder<ExampleLiveObjectA,ExampleFactoryA,Void> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class, ctx -> {
            ExampleFactoryA root = new ExampleFactoryA();
            root.referenceAttribute.set(ctx.get(ExampleFactoryB.class));
            return root;
        });
        builder.addFactory(ExampleFactoryB.class, Scope.SINGLETON, ctx -> new ExampleFactoryB());

        BranchSelector<ExampleFactoryA> subTreeUtility = new BranchSelector<>(builder );
        subTreeUtility.select(ExampleFactoryB.class).start().stop();


    }

    @Test
    public void test_by_class(){
        ExampleFactoryA root = new ExampleFactoryA();

        BranchSelector<ExampleFactoryA> branchSelector = new BranchSelector<>(root);
        ExampleLiveObjectA mock = Mockito.mock(ExampleLiveObjectA.class);
        branchSelector.select(ExampleFactoryA.class).mock(mock);
        assertEquals(mock,root.internal().instance());
    }

    @Test
    public void test_nested(){
        ExampleFactoryA root = new ExampleFactoryA();
        root.referenceAttribute.set(new ExampleFactoryB());
        root.internal().addBackReferences();

        BranchSelector<ExampleFactoryA> branchSelector = new BranchSelector<>(root);
        ExampleLiveObjectB mock = Mockito.mock(ExampleLiveObjectB.class);
        branchSelector.select(ExampleFactoryB.class).mock(mock);
        assertEquals(mock,root.referenceAttribute.get().internal().instance());
        assertFalse(MockUtil.isMock(root.internal().instance()));
    }

    @Test
    public void test_named_nested(){
        FactoryTreeBuilder<ExampleLiveObjectA,ExampleFactoryA,Void> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class, ctx -> {
            ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
            exampleFactoryA.referenceAttribute.set(ctx.get(ExampleFactoryB.class));
            return exampleFactoryA;
        });
        builder.addFactory(ExampleFactoryB.class,Scope.SINGLETON, ctx -> new ExampleFactoryB());
        ExampleFactoryA root = builder.buildTreeUnvalidated();

        BranchSelector<ExampleFactoryA> branchSelector = new BranchSelector<>(root);
        ExampleLiveObjectB mock = Mockito.mock(ExampleLiveObjectB.class);
        branchSelector.select(ExampleFactoryB.class).mock(mock);
        assertEquals(mock,root.referenceAttribute.get().internal().instance());
        assertFalse(MockUtil.isMock(root.internal().instance()));
    }


    @Test
    public void test_name_null(){
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

        BranchSelector<ExampleFactoryA> subTreeUtility = new BranchSelector<>(builder);
        assertEquals("bla",subTreeUtility.select(ExampleFactoryB.class).factory().stringAttribute.get());
    }



}
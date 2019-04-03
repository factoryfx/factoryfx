package io.github.factoryfx.factory;

import io.github.factoryfx.factory.builder.FactoryContext;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.builder.Scope;
import io.github.factoryfx.factory.testfactories.ExampleFactoryA;
import io.github.factoryfx.factory.testfactories.ExampleFactoryB;
import io.github.factoryfx.factory.testfactories.ExampleLiveObjectA;
import io.github.factoryfx.factory.testfactories.ExampleLiveObjectB;
import org.junit.jupiter.api.Test;

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


}
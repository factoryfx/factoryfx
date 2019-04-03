package io.github.factoryfx.factory.mock;

import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.builder.Scope;
import io.github.factoryfx.factory.testfactories.ExampleFactoryA;
import io.github.factoryfx.factory.testfactories.ExampleFactoryB;
import io.github.factoryfx.factory.testfactories.ExampleLiveObjectA;
import io.github.factoryfx.factory.testfactories.ExampleLiveObjectB;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.MockUtil;

import static org.junit.jupiter.api.Assertions.*;

class MockSetterTest {

    @Test
    public void test_by_class(){
        ExampleFactoryA root = new ExampleFactoryA();
        root.internal().addBackReferences();

        MockSetter<ExampleFactoryA> mockSetter = new MockSetter<>(root);
        ExampleLiveObjectA mock = Mockito.mock(ExampleLiveObjectA.class);
        mockSetter.setMock(ExampleFactoryA.class, mock);
        assertEquals(mock,root.internal().instance());
    }

    @Test
    public void test_nested(){
        ExampleFactoryA root = new ExampleFactoryA();
        root.referenceAttribute.set(new ExampleFactoryB());
        root.internal().addBackReferences();

        MockSetter<ExampleFactoryA> mockSetter = new MockSetter<>(root);
        ExampleLiveObjectB mock = Mockito.mock(ExampleLiveObjectB.class);
        mockSetter.setMock(ExampleFactoryB.class, mock);
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
        builder.addFactory(ExampleFactoryB.class, "bla",Scope.SINGLETON, ctx -> new ExampleFactoryB());
        ExampleFactoryA root = builder.buildTreeUnvalidated();

        MockSetter<ExampleFactoryA> mockSetter = new MockSetter<>(root);
        ExampleLiveObjectB mock = Mockito.mock(ExampleLiveObjectB.class);
        mockSetter.setMock(ExampleFactoryB.class,"bla", mock);
        assertEquals(mock,root.referenceAttribute.get().internal().instance());
        assertFalse(MockUtil.isMock(root.internal().instance()));
    }



}
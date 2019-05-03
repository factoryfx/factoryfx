package io.github.factoryfx.javafx;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.testfactories.ExampleLiveObjectA;
import io.github.factoryfx.javafx.editor.DataEditorFactory;
import io.github.factoryfx.javafx.factoryviewmanager.FactoryEditViewFactory;
import org.junit.jupiter.api.Test;

public class FactoryEditViewFactoryTest {
    public static class DummyRoot extends SimpleFactoryBase<ExampleLiveObjectA, DummyRoot> {

        @Override
        public ExampleLiveObjectA createImpl() {
            return null;
        }

    }
    @Test
    public void test_generics(){
        FactoryEditViewFactory<DummyRoot,Void> factoryEditViewFactory = new FactoryEditViewFactory<>();
        factoryEditViewFactory.dataEditorFactory.set(new DataEditorFactory());

    }

}
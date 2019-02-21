package de.factoryfx.javafx.factory.view.factoryviewmanager;

import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.testfactories.ExampleLiveObjectA;
import de.factoryfx.javafx.factory.editor.DataEditorFactory;
import org.junit.jupiter.api.Test;

public class FactoryEditViewFactoryTest {
    public static class DummyRoot extends SimpleFactoryBase<ExampleLiveObjectA,Void, DummyRoot> {

        @Override
        public ExampleLiveObjectA createImpl() {
            return null;
        }

    }
    @Test
    public void test_generics(){
        FactoryEditViewFactory<Void,DummyRoot,Void> factoryEditViewFactory = new FactoryEditViewFactory<>();
        factoryEditViewFactory.dataEditorFactory.set(new DataEditorFactory());

    }

}
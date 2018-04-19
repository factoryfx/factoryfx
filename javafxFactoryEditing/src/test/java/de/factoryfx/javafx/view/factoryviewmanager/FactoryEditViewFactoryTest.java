package de.factoryfx.javafx.view.factoryviewmanager;

import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.atrribute.FactoryReferenceListAttribute;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import de.factoryfx.factory.testfactories.ExampleFactoryB;
import de.factoryfx.factory.testfactories.ExampleLiveObjectA;
import de.factoryfx.factory.testfactories.ExampleLiveObjectB;
import de.factoryfx.javafx.editor.data.DataEditor;
import org.junit.Test;

public class FactoryEditViewFactoryTest {
    public static class DummyRoot extends SimpleFactoryBase<ExampleLiveObjectA,Void, DummyRoot> {

        @Override
        public ExampleLiveObjectA createImpl() {
            return null;
        }

    }
    @Test
    public void test_generics(){
        FactoryEditViewFactory<Void,DummyRoot,Void,DummyRoot,Void> factoryEditViewFactory = new FactoryEditViewFactory<>();
        factoryEditViewFactory.dataEditorFactory.set(new SimpleFactoryBase<DataEditor,Void,DummyRoot>(){
            @Override
            public DataEditor createImpl() {
                return null;
            }
        });

    }

}
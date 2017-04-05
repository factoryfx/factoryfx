package de.factoryfx.javafx.view.factoryviewmanager;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.javafx.editor.data.DataEditor;
import org.junit.Test;

public class FactoryEditViewFactoryTest {

    @Test
    public void test_generics(){
        FactoryEditViewFactory<String,FactoryBase<?,String>> factoryEditViewFactory = new FactoryEditViewFactory<>();
        factoryEditViewFactory.dataEditorFactory.set(new SimpleFactoryBase<DataEditor,Void>(){
            @Override
            public DataEditor createImpl() {
                return null;
            }
        });

    }

}
package de.factoryfx.example.client.view;

import de.factoryfx.example.client.RichClientRoot;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.javafx.editor.DataEditorFactory;
import de.factoryfx.javafx.editor.data.DataEditor;
import de.factoryfx.javafx.util.UniformDesign;
import de.factoryfx.javafx.util.UniformDesignFactory;
import de.factoryfx.javafx.view.factoryviewmanager.FactoryAwareWidget;
import de.factoryfx.javafx.view.factoryviewmanager.FactoryAwareWidgetFactory;

public class ConfigurationViewFactory extends FactoryAwareWidgetFactory {
    public final FactoryReferenceAttribute<UniformDesign, UniformDesignFactory<Void,RichClientRoot>> uniformDesign = new FactoryReferenceAttribute<UniformDesign, UniformDesignFactory<Void,RichClientRoot>>()
        .setupUnsafe(UniformDesignFactory.class)
        .de("uniformDesign")
        .en("uniformDesign");
    public final FactoryReferenceAttribute<DataEditor, DataEditorFactory<Void,RichClientRoot>> dataEditorFactory = new FactoryReferenceAttribute<>();

    @Override
    protected FactoryAwareWidget createWidget() {
        return new ConfigurationView(uniformDesign.instance(), dataEditorFactory.instance());
    }
}

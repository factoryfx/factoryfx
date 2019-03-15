package de.factoryfx.javafx.factory.widget.factory.datatree;

import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.javafx.data.editor.data.DataEditor;
import de.factoryfx.javafx.data.util.UniformDesign;
import de.factoryfx.javafx.data.widget.tree.DataTreeWidget;
import de.factoryfx.javafx.factory.RichClientRoot;
import de.factoryfx.javafx.factory.editor.DataEditorFactory;
import de.factoryfx.javafx.factory.util.UniformDesignFactory;

public class DataTreeWidgetFactory extends SimpleFactoryBase<DataTreeWidget,RichClientRoot> {

    public final FactoryReferenceAttribute<UniformDesign, UniformDesignFactory> uniformDesign = new FactoryReferenceAttribute<>(UniformDesignFactory.class).de("uniformDesign").en("uniformDesign");
    public final FactoryReferenceAttribute<DataEditor, DataEditorFactory> dataEditorFactory = new FactoryReferenceAttribute<>(DataEditorFactory.class);

    @Override
    public DataTreeWidget createImpl() {
        return new DataTreeWidget(dataEditorFactory.instance(),uniformDesign.instance());
    }
}

package io.github.factoryfx.javafx.factory.widget.factory.datatree;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryReferenceAttribute;
import io.github.factoryfx.javafx.factory.editor.data.DataEditor;
import io.github.factoryfx.javafx.factory.util.UniformDesign;
import io.github.factoryfx.javafx.factory.widget.tree.DataTreeWidget;
import io.github.factoryfx.javafx.factory.RichClientRoot;
import io.github.factoryfx.javafx.factory.editor.DataEditorFactory;
import io.github.factoryfx.javafx.factory.util.UniformDesignFactory;

public class DataTreeWidgetFactory extends SimpleFactoryBase<DataTreeWidget,RichClientRoot> {

    public final FactoryReferenceAttribute<RichClientRoot,UniformDesign, UniformDesignFactory> uniformDesign = new FactoryReferenceAttribute<>();
    public final FactoryReferenceAttribute<RichClientRoot,DataEditor, DataEditorFactory> dataEditorFactory = new FactoryReferenceAttribute<>();

    @Override
    public DataTreeWidget createImpl() {
        return new DataTreeWidget(dataEditorFactory.instance(),uniformDesign.instance());
    }
}

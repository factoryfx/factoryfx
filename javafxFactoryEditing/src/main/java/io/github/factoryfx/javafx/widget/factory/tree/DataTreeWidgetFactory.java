package io.github.factoryfx.javafx.widget.factory.tree;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.factory.attribute.primitive.IntegerAttribute;
import io.github.factoryfx.javafx.RichClientRoot;
import io.github.factoryfx.javafx.editor.DataEditor;
import io.github.factoryfx.javafx.editor.DataEditorFactory;
import io.github.factoryfx.javafx.util.UniformDesign;
import io.github.factoryfx.javafx.util.UniformDesignFactory;

public class DataTreeWidgetFactory extends SimpleFactoryBase<DataTreeWidget,RichClientRoot> {

    public final FactoryAttribute<UniformDesign, UniformDesignFactory> uniformDesign = new FactoryAttribute<>();
    public final FactoryAttribute<DataEditor, DataEditorFactory> dataEditorFactory = new FactoryAttribute<>();

    public final IntegerAttribute maxTreeSize = new IntegerAttribute().defaultValue(1000);

    @Override
    protected DataTreeWidget createImpl() {
        return new DataTreeWidget(dataEditorFactory.instance(),uniformDesign.instance(), maxTreeSize.get());
    }
}

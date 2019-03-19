package io.github.factoryfx.javafx.factory.widget.factory.dataview;

import io.github.factoryfx.data.Data;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import io.github.factoryfx.javafx.data.editor.data.DataEditor;
import io.github.factoryfx.javafx.data.util.UniformDesign;
import io.github.factoryfx.javafx.data.widget.dataview.DataViewWidget;
import io.github.factoryfx.javafx.factory.RichClientRoot;
import io.github.factoryfx.javafx.factory.editor.DataEditorFactory;
import io.github.factoryfx.javafx.factory.util.UniformDesignFactory;

public class DataViewWidgetFactory<T extends Data> extends SimpleFactoryBase<DataViewWidget<T>, RichClientRoot> {

    public final FactoryReferenceAttribute<UniformDesign, UniformDesignFactory> uniformDesign =
            new FactoryReferenceAttribute<>(UniformDesignFactory.class).de("uniformDesign").en("uniformDesign");
    public final FactoryReferenceAttribute<DataEditor, DataEditorFactory> dataEditorFactory = new FactoryReferenceAttribute<>(DataEditorFactory.class);


    @Override
    public DataViewWidget<T> createImpl() {
        return new DataViewWidget<>(dataEditorFactory.instance(),uniformDesign.instance());
    }
}

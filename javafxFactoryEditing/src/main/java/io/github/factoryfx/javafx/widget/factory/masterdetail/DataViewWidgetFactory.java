package io.github.factoryfx.javafx.widget.factory.masterdetail;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.javafx.editor.DataEditor;
import io.github.factoryfx.javafx.util.UniformDesign;
import io.github.factoryfx.javafx.RichClientRoot;
import io.github.factoryfx.javafx.editor.DataEditorFactory;
import io.github.factoryfx.javafx.util.UniformDesignFactory;

public class DataViewWidgetFactory<RS extends FactoryBase<?,RS>,L,F extends FactoryBase<L,RS>> extends SimpleFactoryBase<DataViewWidget<RS,L,F>, RichClientRoot> {

    public final FactoryAttribute<RichClientRoot, UniformDesign, UniformDesignFactory> uniformDesign = new FactoryAttribute<>();
    public final FactoryAttribute<RichClientRoot, DataEditor, DataEditorFactory> dataEditorFactory = new FactoryAttribute<>();

    @Override
    protected DataViewWidget<RS,L,F> createImpl() {
        return new DataViewWidget<>(dataEditorFactory.instance(),uniformDesign.instance());
    }
}

package io.github.factoryfx.javafx.factoryviewmanager;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.javafx.editor.DataEditorFactory;
import io.github.factoryfx.javafx.editor.DataEditor;
import io.github.factoryfx.javafx.util.LongRunningActionExecutor;
import io.github.factoryfx.javafx.util.LongRunningActionExecutorFactory;
import io.github.factoryfx.javafx.util.UniformDesign;
import io.github.factoryfx.javafx.util.UniformDesignFactory;
import io.github.factoryfx.javafx.widget.Widget;
import io.github.factoryfx.javafx.widget.factory.WidgetFactory;
import io.github.factoryfx.javafx.widget.factory.diffdialog.DiffDialogBuilder;
import io.github.factoryfx.javafx.widget.factory.diffdialog.DiffDialogBuilderFactory;

/**
 *
 * @param <RS> server root
 */
public class FactoryEditViewFactory<RS extends FactoryBase<?,RS>> extends WidgetFactory {

    public final FactoryAttribute<FactoryEditManager<RS>, FactoryEditManagerFactory<RS>> factoryEditManager = new FactoryAttribute<>();
    public final FactoryAttribute<LongRunningActionExecutor, LongRunningActionExecutorFactory> longRunningActionExecutor = new FactoryAttribute<>();
    public final FactoryAttribute<UniformDesign, UniformDesignFactory> uniformDesign = new FactoryAttribute<>();
    public final FactoryAttribute<DataEditor, DataEditorFactory> dataEditorFactory = new FactoryAttribute<>();
    public final FactoryAttribute<FactoryAwareWidget<RS>,FactoryAwareWidgetFactory<RS>> contentWidgetFactory = new FactoryAttribute<>();
    public final FactoryAttribute<DiffDialogBuilder, DiffDialogBuilderFactory> diffDialogBuilder = new FactoryAttribute<>();

    @Override
    protected Widget createWidget() {
        return new FactoryEditView<>(longRunningActionExecutor.instance(), factoryEditManager.instance(), contentWidgetFactory.instance(), uniformDesign.instance(), dataEditorFactory.instance(), diffDialogBuilder.instance());
    }
}

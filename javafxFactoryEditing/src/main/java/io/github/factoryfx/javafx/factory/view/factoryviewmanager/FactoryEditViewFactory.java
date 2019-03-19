package io.github.factoryfx.javafx.factory.view.factoryviewmanager;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import io.github.factoryfx.javafx.data.editor.data.DataEditor;
import io.github.factoryfx.javafx.factory.editor.DataEditorFactory;
import io.github.factoryfx.javafx.factory.util.LongRunningActionExecutor;
import io.github.factoryfx.javafx.factory.util.LongRunningActionExecutorFactory;
import io.github.factoryfx.javafx.data.util.UniformDesign;
import io.github.factoryfx.javafx.factory.util.UniformDesignFactory;
import io.github.factoryfx.javafx.data.widget.Widget;
import io.github.factoryfx.javafx.factory.widget.factory.WidgetFactory;
import io.github.factoryfx.javafx.factory.widget.factory.diffdialog.DiffDialogBuilder;
import io.github.factoryfx.javafx.factory.widget.factory.diffdialog.DiffDialogBuilderFactory;

/**
 *
 * @param <RS> server root
 */
public class FactoryEditViewFactory<RS extends FactoryBase<?,RS>,S> extends WidgetFactory {
    @SuppressWarnings("unchecked")
    public final FactoryReferenceAttribute<FactoryEditManager<RS,S>, FactoryEditManagerFactory<RS,S>> factoryEditManager = FactoryReferenceAttribute.create(new FactoryReferenceAttribute<>(FactoryEditManagerFactory.class));
    public final FactoryReferenceAttribute<LongRunningActionExecutor, LongRunningActionExecutorFactory> longRunningActionExecutor = new FactoryReferenceAttribute<>(LongRunningActionExecutorFactory.class).de("items").en("items");
    public final FactoryReferenceAttribute<UniformDesign, UniformDesignFactory> uniformDesign = new FactoryReferenceAttribute<>(UniformDesignFactory.class).de("uniformDesign").en("uniformDesign");
    public final FactoryReferenceAttribute<DataEditor, DataEditorFactory> dataEditorFactory = new FactoryReferenceAttribute<>(DataEditorFactory.class);
    @SuppressWarnings("unchecked")
    public final FactoryReferenceAttribute<FactoryAwareWidget<RS>,FactoryAwareWidgetFactory<RS>> contentWidgetFactory = FactoryReferenceAttribute.create(new FactoryReferenceAttribute<>(FactoryAwareWidgetFactory.class));
    public final FactoryReferenceAttribute<DiffDialogBuilder, DiffDialogBuilderFactory> diffDialogBuilder = new FactoryReferenceAttribute<>(DiffDialogBuilderFactory.class);

    @Override
    protected Widget createWidget() {
        return new FactoryEditView<>(longRunningActionExecutor.instance(), factoryEditManager.instance(), contentWidgetFactory.instance(), uniformDesign.instance(), dataEditorFactory.instance(), diffDialogBuilder.instance());
    }
}

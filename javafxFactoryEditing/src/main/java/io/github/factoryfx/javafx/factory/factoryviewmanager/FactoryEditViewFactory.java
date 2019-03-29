package io.github.factoryfx.javafx.factory.factoryviewmanager;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryReferenceAttribute;
import io.github.factoryfx.javafx.factory.RichClientRoot;
import io.github.factoryfx.javafx.factory.editor.DataEditorFactory;
import io.github.factoryfx.javafx.factory.editor.data.DataEditor;
import io.github.factoryfx.javafx.factory.util.LongRunningActionExecutor;
import io.github.factoryfx.javafx.factory.util.LongRunningActionExecutorFactory;
import io.github.factoryfx.javafx.factory.util.UniformDesign;
import io.github.factoryfx.javafx.factory.util.UniformDesignFactory;
import io.github.factoryfx.javafx.factory.widget.Widget;
import io.github.factoryfx.javafx.factory.widget.factory.WidgetFactory;
import io.github.factoryfx.javafx.factory.widget.factory.diffdialog.DiffDialogBuilder;
import io.github.factoryfx.javafx.factory.widget.factory.diffdialog.DiffDialogBuilderFactory;

/**
 *
 * @param <RS> server root
 */
public class FactoryEditViewFactory<RS extends FactoryBase<?,RS>,S> extends WidgetFactory {

    public final FactoryReferenceAttribute<RichClientRoot,FactoryEditManager<RS,S>, FactoryEditManagerFactory<RS,S>> factoryEditManager = new FactoryReferenceAttribute<>();
    public final FactoryReferenceAttribute<RichClientRoot,LongRunningActionExecutor, LongRunningActionExecutorFactory> longRunningActionExecutor = new FactoryReferenceAttribute<>();
    public final FactoryReferenceAttribute<RichClientRoot,UniformDesign, UniformDesignFactory> uniformDesign = new FactoryReferenceAttribute<>();
    public final FactoryReferenceAttribute<RichClientRoot,DataEditor, DataEditorFactory> dataEditorFactory = new FactoryReferenceAttribute<>();
    public final FactoryReferenceAttribute<RichClientRoot,FactoryAwareWidget<RS>,FactoryAwareWidgetFactory<RS>> contentWidgetFactory = new FactoryReferenceAttribute<>();
    public final FactoryReferenceAttribute<RichClientRoot,DiffDialogBuilder, DiffDialogBuilderFactory> diffDialogBuilder = new FactoryReferenceAttribute<>();

    @Override
    protected Widget createWidget() {
        return new FactoryEditView<>(longRunningActionExecutor.instance(), factoryEditManager.instance(), contentWidgetFactory.instance(), uniformDesign.instance(), dataEditorFactory.instance(), diffDialogBuilder.instance());
    }
}
